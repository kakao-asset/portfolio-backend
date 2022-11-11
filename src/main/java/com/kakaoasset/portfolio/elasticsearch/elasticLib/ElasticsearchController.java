package com.kakaoasset.portfolio.elasticsearch.elasticLib;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ElasticsearchController {

    private final ElasticsearchService elasticsearchService;
//    private final ElasticsearchRepositoryKosdaq elasticsearchRepositoryKosdaq;

    @RequestMapping(value = "/lib")
    public String lib(){

        JSONArray result;

        long beforeTime;
        long afterTime;
        double secDiffTime;

        // send request
        beforeTime = System.currentTimeMillis();
        result = elasticsearchService.sampleQuery();
        if (result == null){
            return new JSONObject("{\"error\":\"No Index\"}").toString();
        }
        System.out.println("-------------------------------lib---------------------------");

        afterTime = System.currentTimeMillis();
        secDiffTime = (afterTime - beforeTime);
        System.out.println(secDiffTime);
        return result.toString();
    }
}
