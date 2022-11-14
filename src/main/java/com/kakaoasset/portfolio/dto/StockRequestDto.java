package com.kakaoasset.portfolio.dto;

import com.kakaoasset.portfolio.entity.Member;
import com.kakaoasset.portfolio.entity.Stock;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class StockRequestDto {
    private int price;
    private int quantity;
    private String stockCode;

    public Stock toEntity(Member member) {
        return Stock.builder()
                .member(member)
                .stockCode(stockCode)
                .avgPrice(price)
                .quantity(quantity)
                .build();
    }
}
