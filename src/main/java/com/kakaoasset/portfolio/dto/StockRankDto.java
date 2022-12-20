package com.kakaoasset.portfolio.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StockRankDto {
    private String rank;
    private String name;
    private String symbolCode;
}
