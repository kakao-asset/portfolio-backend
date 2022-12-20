package com.kakaoasset.portfolio.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StockDto {
    private String name;
    private String symbolCode;
}
