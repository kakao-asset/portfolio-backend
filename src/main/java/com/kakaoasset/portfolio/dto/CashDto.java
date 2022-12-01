package com.kakaoasset.portfolio.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor //  cannot deserialize from Object value (no delegate- or property-based Creator)
public class CashDto {
    int cash;

    public CashDto(int cash) {
        this.cash = cash;
    }
}
