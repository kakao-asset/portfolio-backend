package com.kakaoasset.portfolio.dto;

import com.kakaoasset.portfolio.entity.Member;
import com.kakaoasset.portfolio.entity.Stock;
import lombok.Getter;

@Getter
public class StockRequestDto {
    private int price;
    private int quantity;
    private String stockName;

    public Stock toEntity(Member member) {
        return Stock.builder()
                .member(member)
                .stockName(stockName)
                .avgPrice(price)
                .quantity(quantity)
                .build();
    }
}
