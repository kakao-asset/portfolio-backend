package com.kakaoasset.portfolio.dto;

import lombok.Getter;

import java.util.List;
@Getter
public class HistoryListDto {
    private String date;
    private List<HistoryResponseDto> historyList;

    public HistoryListDto(String date, List<HistoryResponseDto> historyList){
        this.date = date;
        this.historyList = historyList;
    }
}
