package com.example.kakaoasset.elasticLib;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ElasticsearchController {

    private final ElasticsearchService elasticsearchService;
//    private final ElasticsearchRepositoryKosdaq elasticsearchRepositoryKosdaq;

    @RequestMapping(value = "/lib")
    public String lib(){

        JSONArray jsonarr = new JSONArray();

        try {
            SearchResponse searchResponse = elasticsearchService.sampleQuery();

            JSONObject json = new JSONObject(searchResponse);

            System.out.println("-------------------------------lib---------------------------");

            System.out.println("json = " + json);

            for (int i = 0; i < json.getJSONObject("hits").getJSONArray("hits").length(); i++) {
                JSONObject temp = ((JSONObject) json.getJSONObject("hits").getJSONArray("hits").get(i)).getJSONObject("sourceAsMap");
                String name = temp.getString("name");
                String code = temp.getString("symbolCode");
                int tradePrice = temp.getInt("tradePrice");
                System.out.println(name + ", " + code + ", " + tradePrice);

                JSONObject itme = new JSONObject();
                itme.put("name", temp.getString("name"));
                itme.put("symbolCode", temp.getString("symbolCode"));
                itme.put("tradePrice", temp.getInt("tradePrice"));

                jsonarr.put(itme);

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return jsonarr.toString();
    }
}
