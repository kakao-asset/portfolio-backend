package com.example.kakaoasset.elasticAPI.controller;

import com.example.kakaoasset.elasticAPI.service.RealtimeStockService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class RealtimeStockController {

    @Autowired
    private RealtimeStockService realtimeStockService;

    @RequestMapping(value = "/main/realtime", method = RequestMethod.GET)
    public String tset(@RequestParam String stock_name){

        String result = null;

        long beforeTime;
        long afterTime;
        double secDiffTime;

        beforeTime = System.currentTimeMillis();

        result = realtimeStockService.selectRealtimeStock(stock_name);

        afterTime = System.currentTimeMillis();
        secDiffTime = (afterTime - beforeTime);

        System.out.println("time : "+secDiffTime);
        return result;
    }
}
