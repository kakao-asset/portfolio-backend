package com.kakaoasset.portfolio.dto;

import com.fasterxml.jackson.databind.DatabindException;
import lombok.Builder;
import lombok.Getter;

import java.sql.Date;

@Builder
@Getter
public class HistoryResponseDto {
    private String stockName;
    private boolean tradeType;
    private Date tradeDate;
    private String tradeTime;
    private int price;
    private int quantity;


}
