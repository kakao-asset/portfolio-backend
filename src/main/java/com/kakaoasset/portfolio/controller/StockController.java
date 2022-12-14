package com.kakaoasset.portfolio.controller;

import com.kakaoasset.portfolio.dto.BasicResponse;
import com.kakaoasset.portfolio.dto.CashDto;
import com.kakaoasset.portfolio.dto.StockRequestDto;
import com.kakaoasset.portfolio.dto.StockResponseDto;
import com.kakaoasset.portfolio.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StockController {

    private final StockService stockService;

    @PostMapping("/stock/buy/{userId}")
    public ResponseEntity<BasicResponse> buyStock(@PathVariable("userId") Long userId, @RequestBody StockRequestDto stockRequestDto){
        StockResponseDto stockResponseDto = stockService.buyStock(userId, stockRequestDto);
        BasicResponse response = BasicResponse.builder()
                .code(HttpStatus.OK.value())
                .message("매수에 성공했습니다. ")
                .data(stockResponseDto)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/stock/sell/{userId}")
    public ResponseEntity<BasicResponse> sellStock(@PathVariable("userId") Long userId, @RequestBody StockRequestDto stockRequestDto){
        Object stockResponseDto = stockService.sellStock(userId, stockRequestDto);
        BasicResponse response;
        if(stockResponseDto.equals("delete")){
            response = BasicResponse.builder()
                    .code(HttpStatus.OK.value())
                    .message("전량 매도에 성공했습니다.")
                    .data(Collections.emptyList())
                    .build();

        } else if(stockResponseDto.equals("sell count error")){
            response = BasicResponse.builder()
                    .code(HttpStatus.OK.value())
                    .message("현재 보유 잔량보다 매도 수량이 큽니다.")
                    .data(Collections.emptyList())
                    .build();
        }
        else {
            response = BasicResponse.builder()
                    .code(HttpStatus.OK.value())
                    .message("매도에 성공했습니다.")
                    .data(stockResponseDto)
                    .build();
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/stock/{userId}")
    public ResponseEntity<BasicResponse> getUserStocks(@PathVariable("userId") Long userId) {
        BasicResponse response = BasicResponse.builder()
                .code(HttpStatus.OK.value())
                .message("보유 주식 조회에 성공했습니다.")
                .data(stockService.getStock(userId))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/stock")
    public ResponseEntity<BasicResponse> getAllStocks() {

        BasicResponse response = BasicResponse.builder()
                .code(HttpStatus.OK.value())
                .message("총 주식 목록 조회에 성공했습니다.")
                .data(stockService.getStockList())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping("/stock/search")
    public ResponseEntity<BasicResponse> getMatch(@RequestParam ("word") String word) {
        BasicResponse response = BasicResponse.builder()
                .code(HttpStatus.OK.value())
                .message("검색어 조회에 성공했습니다.")
                .data(stockService.getMatchStockList(word))
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/stock/rank")
    public ResponseEntity<BasicResponse> getRank() {
        BasicResponse response = BasicResponse.builder()
                .code(HttpStatus.OK.value())
                .message("다음 실시간 검색어 순위 조회에 성공했습니다.")
                .data(stockService.getDaumRank())
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/stock/history/{userId}")
    public ResponseEntity<BasicResponse> getStockHistory(@PathVariable("userId") Long userId) {
        BasicResponse response = BasicResponse.builder()
                .code(HttpStatus.OK.value())
                .message("매도 매수 현황 조회에 성공했습니다.")
                .data(stockService.getStockHistory(userId))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/stock/trend/{userId}")
    public ResponseEntity<BasicResponse> getTrend(@PathVariable("userId") Long userId) {
        BasicResponse response = BasicResponse.builder()
                .code(HttpStatus.OK.value())
                .message("자산 동향 내역 조회에 성공했습니다.")
                .data(stockService.getTrendList(userId))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/cash/{userId}")
    public ResponseEntity<BasicResponse> getCash(@PathVariable("userId") Long userId) {
        BasicResponse response = BasicResponse.builder()
                .code(HttpStatus.OK.value())
                .message("현금 조회에 성공했습니다.")
                .data(stockService.getCash(userId))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/cash/{userId}")
    public ResponseEntity<BasicResponse> updateCash(@PathVariable("userId") Long userId, @RequestBody CashDto cashDto) {
        BasicResponse response = BasicResponse.builder()
                .code(HttpStatus.OK.value())
                .message("보유 현금 금액 변경에 성공했습니다.")
                .data(stockService.updateCash(userId, cashDto))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
