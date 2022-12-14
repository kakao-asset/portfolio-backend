package com.kakaoasset.portfolio.service;

import com.kakaoasset.portfolio.dto.*;
import com.kakaoasset.portfolio.entity.Asset;
import com.kakaoasset.portfolio.entity.Stock;
import com.kakaoasset.portfolio.entity.StockHistory;
import com.kakaoasset.portfolio.entity.Trend;
import com.kakaoasset.portfolio.repostiory.*;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StockService {

    private final MemberRepository memberRepository;
    private final StockRepository stockRepository;
    private final StockHistoryRepository stockHistoryRepository;
    private final TrendRepository trendRepository;
    private final AssetRepository assetRepository;

    @Value("${elasticsearch.host}")
    private String host;

    @Value("${index.stock-rank-index}")
    private String rankIndex;

    @Value("${index.stock-list-index}")
    private String listIndex;

    public StockResponseDto buyStock(Long id, StockRequestDto stockRequestDto){
        Stock stock = stockRepository.findByStockNameAndMember_MemberId(stockRequestDto.getStockName(), id);
        if(stock == null){
            stock = stockRequestDto.toStockEntity(memberRepository.findByMemberId(id));
        } else {
            Long originQuantity = stock.getQuantity();
            Long originPrice = stock.getAvgPrice();
            Long quantity = stockRequestDto.getQuantity();
            Long price = stockRequestDto.getPrice();
            stock.setQuantity(originQuantity + quantity);
            stock.setAvgPrice((originQuantity * originPrice + quantity * price)/ stock.getQuantity());
        }

        StockHistory stockHistory = stockRequestDto.toStockHistoryEntity(memberRepository.findByMemberId(id), true);

        Optional<Asset> asset = assetRepository.findByMemberId(id);

        asset.get().updateCash(asset.get().getCash() - (stockRequestDto.getPrice() * stockRequestDto.getQuantity()));
        asset.get().updateBuyPrice(asset.get().getBuyPrice() + (stockRequestDto.getPrice() * stockRequestDto.getQuantity()));

        stockRepository.save(stock);
        stockHistoryRepository.save(stockHistory);
        assetRepository.save(asset.get());

        return StockResponseDto.builder()
                .stockName(stock.getStockName())
                .stockCode(stock.getStockCode())
                .sectorName(stock.getSectorName())
                .sectorCode(stock.getSectorCode())
                .avgPrice(stock.getAvgPrice())
                .quantity(stock.getQuantity())
                .build();
    }

    @Transactional
    public Object sellStock(Long id, StockRequestDto stockRequestDto){
        Stock stock = stockRepository.findByStockNameAndMember_MemberId(stockRequestDto.getStockName(), id);

        Long quantity = stock.getQuantity() - stockRequestDto.getQuantity();

        StockHistory stockHistory = stockRequestDto.toStockHistoryEntity(memberRepository.findByMemberId(id), false);

        Optional<Asset> asset = assetRepository.findByMemberId(id);
        asset.get().updateCash(asset.get().getCash() + (stockRequestDto.getPrice() * stockRequestDto.getQuantity()));
        asset.get().updateBuyPrice(asset.get().getBuyPrice() - (stock.getAvgPrice() * stock.getQuantity()));

        stockHistoryRepository.save(stockHistory);
        assetRepository.save(asset.get());

        // ?????? ??????
        if (quantity == 0) {
            stockRepository.deleteByStockNameAndMember_MemberId(stockRequestDto.getStockName(), id);
            return "delete";
        } else if(quantity < 0){
            return "sell count error";
        } else {
            stock.setQuantity(quantity);
            stockRepository.save(stock);
            return StockResponseDto.builder()
                    .stockName(stock.getStockName())
                    .stockCode(stock.getStockCode())
                    .sectorName(stock.getSectorName())
                    .sectorCode(stock.getSectorCode())
                    .avgPrice(stock.getAvgPrice())
                    .quantity(stock.getQuantity())
                    .build();
        }
    }

    public List<StockResponseDto> getStock(Long id){
        List<Stock> stocks = stockRepository.findByMember_MemberId(id);
        List<StockResponseDto> stocksDto = new LinkedList<>();
        for(Stock s : stocks) {
            stocksDto.add(s.toDto());
        }

        return stocksDto;
    }

    public Object getStockList(){
        String result;
        // make request for elasticsearch api
        RestTemplate rt = new RestTemplate();
        rt.getInterceptors().add((request, body, execution) -> {
            ClientHttpResponse response = execution.execute(request,body);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return response;
        });
        final HttpHeaders headers = new HttpHeaders();
        final HttpEntity<?> entity = new HttpEntity<>(headers);
        try {
            // send request to elasticsearch
            String uri = "http://"+ host + ":9200/"+listIndex+"/_search?size=2000";
            result = rt.exchange(uri, HttpMethod.GET, entity, String.class).getBody();

        }catch (HttpClientErrorException e){
            // no index
            System.out.println(e);
            return new JSONObject("{\"error\":\"No Index\", \"index\":\""+listIndex+"\"}").toString();
        }

        JSONObject json = new JSONObject(result);
        List<StockDto> stockList = new LinkedList<>();
        // String dataCnt = String.valueOf(json.getJSONObject("hits").getJSONObject("total").getNumber("value")); // ????????? ??????

        for (int i = 0; i < json.getJSONObject("hits").getJSONArray("hits").length(); i++) {
            JSONObject res = ((JSONObject) json.getJSONObject("hits").getJSONArray("hits").get(i)).getJSONObject("_source");

            StockDto stockDto = StockDto.builder()
                    .name(res.get("name").toString())
                    .symbolCode(res.get("symbolCode").toString())
                    .build();

            stockList.add(stockDto);
        }

        return stockList;
    }

    public Object getMatchStockList(String word){
        String result;
        // make request for elasticsearch api
        RestTemplate rt = new RestTemplate();
        rt.getInterceptors().add((request, body, execution) -> {
            ClientHttpResponse response = execution.execute(request,body);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return response;
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = "http://"+host+":9200/"+listIndex+"/_search?size=15";
        String query = "{\n" +
                "    \"query\" :{\n" +
                "        \"prefix\": {\n" +
                "            \"name\" : \"" +word+ "\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        HttpEntity<String> request = new HttpEntity<>(query, headers);

        try {
            result = rt.postForObject(url, request, String.class);
        }catch (HttpClientErrorException e){
            // no index
            System.out.println(e);
            return new JSONObject("{\"error\":\"No Index\", \"index\":\""+listIndex+"\"}").toString();
        }

        JSONObject json = new JSONObject(result);
        List<StockDto> stockList = new LinkedList<>();

        for (int i = 0; i < json.getJSONObject("hits").getJSONArray("hits").length(); i++) {
            JSONObject res = ((JSONObject) json.getJSONObject("hits").getJSONArray("hits").get(i)).getJSONObject("_source");
            StockDto stockDto = StockDto.builder()
                    .name(res.get("name").toString())
                    .symbolCode(res.get("symbolCode").toString())
                    .build();

            stockList.add(stockDto);
        }

        return stockList;
    }

    public Object getDaumRank(){
        String result;
        // make request for elasticsearch api
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/json");
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        try {
            // send request to elasticsearch
            String uri = "http://"+host+":9200/"+rankIndex+"/_search?";
            result = rt.exchange(uri,
                    HttpMethod.GET,
                    entity,
                    String.class).getBody();

        }catch (HttpClientErrorException e){
            // no index
            System.out.println(e);
            return new JSONObject("{\"error\":\"No Index\", \"index\":\""+rankIndex+"\"}").toString();
        }

        JSONObject json = new JSONObject(result);
        List<StockRankDto> stockList = new LinkedList<>();
        // String dataCnt = String.valueOf(json.getJSONObject("hits").getJSONObject("total").getNumber("value")); // ????????? ??????

        for (int i = 0; i < json.getJSONObject("hits").getJSONArray("hits").length(); i++) {
            JSONObject res = ((JSONObject) json.getJSONObject("hits").getJSONArray("hits").get(i)).getJSONObject("_source");

            StockRankDto stockRankDto = StockRankDto.builder()
                    .rank(res.get("rank").toString())
                    .name(res.get("name").toString())
                    .symbolCode(res.get("symbolCode").toString())
                    .build();

            stockList.add(stockRankDto);
        }

        return stockList;


    }

    public List<HistoryResponseDto> getStockHistory(Long id){

        // history??? ?????? ???
        List<StockHistory> stockHistoryList = stockHistoryRepository.findByMember_MemberId(id);

        List<HistoryResponseDto> historyList = new ArrayList<>();

        JSONObject json = new JSONObject();
        for(StockHistory sh: stockHistoryList) {
            HistoryResponseDto data = HistoryResponseDto.builder()
                    .stockName(sh.getStockName())
                    .price(sh.getPrice())
                    .quantity(sh.getQuantity())
                    .tradeType(sh.isTradeType())
                    .tradeDate(String.valueOf(sh.getTradeDate()))
                    .tradeTime(sh.getTradeTime())
                    .build();
            historyList.add(data);
        }

        return historyList;
    }

    public List<TrendDto> getTrendList(Long id){
        List<Trend> trends = trendRepository.findByMemberId(id);
        MultiValueMap<String, TrendDto.TrendData> trendMap= new LinkedMultiValueMap<>();

        for(Trend t: trends) {
            trendMap.add(t.getDate().toString(), t.toDto());
        }

        List<TrendDto> trendDtoList = new ArrayList<>();

        List<String> keyList = new ArrayList<>(trendMap.keySet());
        Collections.sort(keyList);
        for(String date: keyList){
            TrendDto trendDto = TrendDto.builder()
                    .date(date)
                    .trendDataList(trendMap.get(date))
                    .build();

            trendDtoList.add(trendDto);
        }

        return trendDtoList;
    }

    public Object getCash(Long id){
        return assetRepository.findByMemberId(id).isPresent() ? new CashDto(assetRepository.findByMemberId(id).get().getCash()) : Collections.emptyList();
    }

    public List<Object> updateCash(Long id, CashDto cashDto){
        Asset asset = assetRepository.findByMemberId(id).orElse(null);
        if(asset == null) {
            asset = Asset.builder()
                    .id(id)
                    .cash(cashDto.getCash())
                    .buyPrice(0L)
                    .build();
        } else {
            asset.updateCash(asset.getCash() + cashDto.getCash());
        }

        assetRepository.save(asset);

        return Collections.emptyList();
    }
}