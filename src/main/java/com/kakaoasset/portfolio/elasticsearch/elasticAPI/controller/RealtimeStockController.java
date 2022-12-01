package com.kakaoasset.portfolio.elasticsearch.elasticAPI.controller;
import com.kakaoasset.portfolio.elasticsearch.elasticAPI.service.RealtimeStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class RealtimeStockController {

    @Autowired
    private RealtimeStockService realtimeStockService;

    @RequestMapping(value = "/main/realtime", method = RequestMethod.GET)
    public String realtimeStock(@RequestParam String stock_name){

        String result = null;

        result = realtimeStockService.selectRealtimeStock(stock_name);

        return result;
    }
}

