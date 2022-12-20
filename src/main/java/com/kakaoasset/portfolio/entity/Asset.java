package com.kakaoasset.portfolio.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Asset {

    @Id
    private Long memberId;

    private Long cash;

    @ColumnDefault("0") //default 0
    private Long buyPrice;

    @Builder
    public Asset(Long id, Long cash, Long buyPrice){
        this.memberId= id;
        this.buyPrice = buyPrice;
        this.cash = cash;
    }

    public void updateCash(Long cash){
        this.cash = cash;
    }

    public void updateBuyPrice(Long buyPrice){
        this.buyPrice = buyPrice;
    }
}
