package com.kakaoasset.portfolio.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class TrendDto {

    private String date;

    private List<TrendData> trndDataList;

    @Getter
    @Builder
    public static class TrendData {
        private Integer avgPrice;
        private Integer quantity;
        private String stockName;
    }
}
