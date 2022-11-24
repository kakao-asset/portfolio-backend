package com.kakaoasset.portfolio.service;

import com.kakaoasset.portfolio.dto.*;
import com.kakaoasset.portfolio.entity.Stock;
import com.kakaoasset.portfolio.entity.StockHistory;
import com.kakaoasset.portfolio.repostiory.MemberRepository;
import com.kakaoasset.portfolio.repostiory.StockHistoryRepository;
import com.kakaoasset.portfolio.repostiory.StockRepository;
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

import java.sql.Date;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StockService {

    private final MemberRepository memberRepository;
    private final StockRepository stockRepository;
    private final StockHistoryRepository stockHistoryRepository;


    @Value("${elasticsearch.host}")
    private String host;

    public StockResponseDto buyStock(Long id, StockRequestDto stockRequestDto){
        Stock stock = stockRepository.findByStockNameAndMember_MemberId(stockRequestDto.getStockName(), id);
        System.out.println(stockRequestDto.getTradeTime());
        System.out.println(stockRequestDto.getTradeTime());
        System.out.println(stockRequestDto.getTradeTime());
        if(stock == null){
            stock = stockRequestDto.toStockEntity(memberRepository.findByMemberId(id));
        } else {
            int originQuantity = stock.getQuantity();
            int originPrice = stock.getAvgPrice();
            int quantity = stockRequestDto.getQuantity();
            int price = stockRequestDto.getPrice();
            stock.setQuantity(originQuantity + quantity);
            stock.setAvgPrice((originQuantity * originPrice + quantity * price)/ stock.getQuantity());
        }
        stockRepository.save(stock);

        StockHistory stockHistory = stockRequestDto.toStockHistoryEntity(memberRepository.findByMemberId(id), true);
        stockHistoryRepository.save(stockHistory);

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

        int quantity = stock.getQuantity() - stockRequestDto.getQuantity();

        StockHistory stockHistory = stockRequestDto.toStockHistoryEntity(memberRepository.findByMemberId(id), false);
        stockHistoryRepository.save(stockHistory);

        // 전량 매도
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
        String index = "stock-code-list";
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
            String uri = "http://"+ host + ":9200/"+index+"/_search?size=2000";
            result = rt.exchange(uri, HttpMethod.GET, entity, String.class).getBody();

        }catch (HttpClientErrorException e){
            // no index
            System.out.println(e);
            return new JSONObject("{\"error\":\"No Index\", \"index\":\""+index+"\"}").toString();
        }

        JSONObject json = new JSONObject(result);
        List<StockDto> stockList = new LinkedList<>();
        // String dataCnt = String.valueOf(json.getJSONObject("hits").getJSONObject("total").getNumber("value")); // 데이터 갯수

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
        String index = "stock-code-list";
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

        String url = "http://"+host+":9200/"+index+"/_search?size=15";
        System.out.println(word);
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
            return new JSONObject("{\"error\":\"No Index\", \"index\":\""+index+"\"}").toString();
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
        String index = "stock-rank";
        String result;
        // make request for elasticsearch api
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/json");
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        try {
            // send request to elasticsearch
            String uri = "http://"+host+":9200/"+index+"/_search?";
            System.out.println("uri : " + uri);
            result = rt.exchange(uri,
                    HttpMethod.GET,
                    entity,
                    String.class).getBody();

        }catch (HttpClientErrorException e){
            // no index
            System.out.println(e);
            return new JSONObject("{\"error\":\"No Index\", \"index\":\""+index+"\"}").toString();
        }

        JSONObject json = new JSONObject(result);
        List<StockRankDto> stockList = new LinkedList<>();
        // String dataCnt = String.valueOf(json.getJSONObject("hits").getJSONObject("total").getNumber("value")); // 데이터 갯수


        for (int i = 0; i < json.getJSONObject("hits").getJSONArray("hits").length(); i++) {
            JSONObject res = ((JSONObject) json.getJSONObject("hits").getJSONArray("hits").get(i)).getJSONObject("_source");

            System.out.println(json.getJSONObject("hits").getJSONArray("hits").length());
            System.out.println(json.getJSONObject("hits").getJSONArray("hits"));

            StockRankDto stockRankDto = StockRankDto.builder()
                    .rank(res.get("rank").toString())
                    .name(res.get("name").toString())
                    .symbolCode(res.get("symbolCode").toString())
                    .build();

            stockList.add(stockRankDto);
        }

        return stockList;


    }

    public MultiValueMap<String, HistoryResponseDto> getStockHistory(Long id){

        List<StockHistory> stockHistoryList = stockHistoryRepository.findByMember_MemberId(id);
        MultiValueMap<String, HistoryResponseDto> historyMap = new LinkedMultiValueMap<>();

        for(StockHistory sh: stockHistoryList) {
            HistoryResponseDto data = HistoryResponseDto.builder()
                    .stockName(sh.getStockName())
                    .price(sh.getPrice())
                    .quantity(sh.getQuantity())
                    .tradeType(sh.isTradeType())
                    .tradeTime(sh.getTradeTime())
                    .build();
            historyMap.add(sh.getTradeDate().toString(), data);
        }
        return  historyMap;
    }
}