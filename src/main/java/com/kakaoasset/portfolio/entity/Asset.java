package com.kakaoasset.portfolio.entity;

import com.kakaoasset.portfolio.dto.CashDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Asset {

    @Id
    private Long memberId;

    private int cash;

    private int buyPrice;

    @Builder
    public Asset(Long id, int cash){
        this.memberId= id;
        this.cash = cash;
    }

    public Asset updateCash(int cash){
        this.cash = cash;
        return this;
    }
}
