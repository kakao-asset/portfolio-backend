package com.kakaoasset.portfolio.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StockHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String stockName;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean tradeType;

    private Long price;

    private Long quantity;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date tradeDate;

    @Column(name = "trade_time")
    private String tradeTime;
}
