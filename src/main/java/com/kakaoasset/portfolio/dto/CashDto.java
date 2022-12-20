package com.kakaoasset.portfolio.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor //  cannot deserialize from Object value (no delegate- or property-based Creator)
public class CashDto {
    Long cash;

    public CashDto(Long cash) {
        this.cash = cash;
    }
}
