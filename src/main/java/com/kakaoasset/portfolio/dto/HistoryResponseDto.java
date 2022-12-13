package com.kakaoasset.portfolio.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class HistoryResponseDto {
    private String stockName;
    private boolean tradeType;
    private String tradeDate;
    private String tradeTime;
    private Long price;
    private Long quantity;
}
