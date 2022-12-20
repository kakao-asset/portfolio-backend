package com.kakaoasset.portfolio.entity;

import com.kakaoasset.portfolio.dto.TrendDto;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "trend")
@Getter
public class Trend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "avg_price")
    private Integer avgPrice;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "stock_code")
    private String stockCode;

    @Column(name = "stock_name")
    private String stockName;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "date")
    private LocalDate date;

    public TrendDto.TrendData toDto(){
        return TrendDto.TrendData.builder()
                .stockName(this.stockName)
                .avgPrice(this.avgPrice)
                .quantity(this.quantity)
                .build();
    }
}