package com.kakaoasset.portfolio.elasticsearch.elasticAPI.controller;

import com.kakaoasset.portfolio.elasticsearch.elasticAPI.service.NewsStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class NewsStockController {

    @Autowired
    private NewsStockService newsStockService;

    @RequestMapping(value = "/main/news", method = RequestMethod.GET)
    public String news(@RequestParam String stockCode) {
        String result = "";

        result = newsStockService.selectNewsStock(stockCode);

        return result;
    }


}