package com.example.kakaoasset.elasticLib;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchResponse;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class ElasticsearchController {

    private final ElasticsearchService elasticsearchService;
//    private final ElasticsearchRepositoryKosdaq elasticsearchRepositoryKosdaq;

    @RequestMapping(value = "/lib")
    public String lib(){

        try {
            SearchResponse searchResponse = elasticsearchService.sampleQuery();

            JSONObject json = new JSONObject(searchResponse);

            System.out.println("-------------------------------lib---------------------------");

            for (int i = 0; i < json.getJSONObject("hits").getJSONArray("hits").length(); i++) {
                JSONObject temp = ((JSONObject) json.getJSONObject("hits").getJSONArray("hits").get(i)).getJSONObject("sourceAsMap");
                String name = temp.getString("name");
                String code = temp.getString("code");
                System.out.println(name + ", " + code);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        for (ElasticsearchDocument doc:elasticsearchRepositoryKosdaq.findAll()) {
//            System.out.println(doc.getName());
//
//        }


        return "main.html";
    }
}
