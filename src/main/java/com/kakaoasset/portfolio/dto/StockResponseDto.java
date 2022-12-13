package com.kakaoasset.portfolio.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter // ResponseEntity가 Json으로 변환을 위해 Jackson.ObjectMapper를 이용해 변환 -> 이때 Getter 필요
public class StockResponseDto {
    private Long avgPrice;
    private Long quantity;
    private String stockName;
    private String stockCode;
    private String sectorCode;
    private String sectorName;
}
