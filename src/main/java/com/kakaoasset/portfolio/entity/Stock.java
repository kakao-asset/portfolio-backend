package com.kakaoasset.portfolio.entity;

import com.kakaoasset.portfolio.dto.StockResponseDto;
import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "stock_name")
    private String stockName;

    @Column(name = "stock_code")
    private String stockCode;

    @Column(name = "sector_code")
    private String sectorCode;

    @Column(name = "sector_name")
    private String sectorName;

    private int quantity;

    private int avgPrice;

    public StockResponseDto toDto(){
        return StockResponseDto.builder()
                .stockName(stockName)
                .stockCode(stockCode)
                .quantity(quantity)
                .avgPrice(avgPrice)
                .sectorName(sectorName)
                .sectorCode(sectorCode)
                .build();
    }
}
