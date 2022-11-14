package com.kakaoasset.portfolio.elasticsearch.elasticLib;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ElasticsearchService {

    private final RestHighLevelClient client;

    private static final String INDEX = "stock-data";

    public JSONArray sampleQuery() {

        JSONArray jsonarr = new JSONArray();
        JSONObject json;

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .size(10000);
        SearchRequest searchRequest = new SearchRequest(INDEX)
                .source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ElasticsearchStatusException e){
            // no index
            return null;
        }

        json= new JSONObject(searchResponse);

        for (int i = 0; i < json.getJSONObject("hits").getJSONArray("hits").length(); i++) {
            JSONObject temp = ((JSONObject) json.getJSONObject("hits").getJSONArray("hits").get(i)).getJSONObject("sourceAsMap");
//            JSONObject itme = new JSONObject();
//            itme.put("name", temp.getString("name"));
//            itme.put("symbolCode", temp.getString("symbolCode"));
//            itme.put("tradePrice", temp.getString("tradePrice"));
//            itme.put("timestamp", temp.getString("@timestamp"));
            jsonarr.put(temp);
        }

        System.out.println("lib "+json.getJSONObject("hits").getJSONArray("hits").length()+" stock data success");
        return jsonarr;
    }

}