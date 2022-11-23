package com.kakaoasset.portfolio.dto;

import com.kakaoasset.portfolio.entity.Member;
import com.kakaoasset.portfolio.entity.Stock;
import com.kakaoasset.portfolio.entity.StockHistory;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
public class StockRequestDto {
    private int price;
    private int quantity;
    private String stockName;
    private String stockCode;
    private String sectorCode;
    private String sectorName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date tradeDate;

    private String tradeTime;


    public Stock toStockEntity(Member member) {
        return Stock.builder()
                .member(member)
                .stockName(stockName)
                .avgPrice(price)
                .sectorCode(sectorCode)
                .stockCode(stockCode)
                .sectorName(sectorName)
                .quantity(quantity)
                .build();
    }

    public StockHistory toStockHistoryEntity(Member member, boolean type){
        return StockHistory.builder()
                .member(member)
                .stockName(stockName)
                .tradeType(type)
                .price(price)
                .quantity(quantity)
                .tradeDate(tradeDate)
                .tradeTime(tradeTime)
                .build();
    }
}
