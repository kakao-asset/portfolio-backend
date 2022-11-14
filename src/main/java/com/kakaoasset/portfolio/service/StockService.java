package com.kakaoasset.portfolio.service;

import com.kakaoasset.portfolio.dto.StockRequestDto;
import com.kakaoasset.portfolio.dto.StockResponseDto;
import com.kakaoasset.portfolio.entity.Member;
import com.kakaoasset.portfolio.entity.Stock;
import com.kakaoasset.portfolio.repostiory.MemberRepository;
import com.kakaoasset.portfolio.repostiory.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final MemberRepository memberRepository;

    public StockResponseDto buyStock(Long id, StockRequestDto stockDto){
        Stock stock = stockRepository.findByStockNameAndMember_MemberId(stockDto.getStockName(), id);

        if(stock == null){
            stock = stockDto.toEntity(memberRepository.findByMemberId(id));
        } else {
            int originQuantity = stock.getQuantity();
            int originPrice = stock.getAvgPrice();
            int quantity = stockDto.getQuantity();
            int price = stockDto.getPrice();
            stock.setQuantity(originQuantity + quantity);
            stock.setAvgPrice((originQuantity * originPrice + quantity * price)/ stock.getQuantity());
        }

        stockRepository.save(stock);
        return StockResponseDto.builder()
                .stockName(stock.getStockName())
                .avgPrice(stock.getAvgPrice())
                .quantity(stock.getQuantity())
                .sectorCode(stock.getSectorCode())
                .build();
    }

    @Transactional
    public Object sellStock(Long id, StockRequestDto stockDto){
        Stock stock = stockRepository.findByStockNameAndMember_MemberId(stockDto.getStockName(), id);

        int quantity = stock.getQuantity() - stockDto.getQuantity();
        // 전량 매도
        if (quantity == 0) {
            stockRepository.deleteByStockNameAndMember_MemberId(stockDto.getStockName(), id);
            return "delete";
        } else if(quantity < 0){
            return "sell count error";
        } else {
            stock.setQuantity(quantity);
            stock.setSectorCode(stock.getSectorCode());
            stockRepository.save(stock);
            return StockResponseDto.builder()
                    .stockName(stock.getStockName())
                    .avgPrice(stock.getAvgPrice())
                    .quantity(stock.getQuantity())
                    .sectorCode(stock.getSectorCode())
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
}
