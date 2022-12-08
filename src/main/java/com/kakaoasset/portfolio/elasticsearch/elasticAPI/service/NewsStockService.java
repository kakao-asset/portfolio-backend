package com.kakaoasset.portfolio.elasticsearch.elasticAPI.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Collections;

@Service
public class NewsStockService {
    public Object selectNewsStock(String stockCode) {
        String result = "";
        JSONArray jsonarr = new JSONArray();
        String detail_url = "https://finance.daum.net/quotes/";
        System.out.println("rs test");
        // make request for elasticsearch api
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            ClientHttpResponse response = execution.execute(request,body);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return response;
        });

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Referer","http://finance.daum.net");
        headers.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36 OPR/58.0.3135.127");
        final HttpEntity<?> entity = new HttpEntity<>(headers);
        try {
            // send request to elasticsearch
            result = restTemplate.exchange("https://finance.daum.net/content/news?page=1&perPage=5&category=economy&searchType=all&keyword=" + stockCode, HttpMethod.GET, entity, String.class).getBody();
        }catch (HttpClientErrorException e){
            // no index
            return Collections.emptyList();
        }

        // 뉴스 크롤링에 대한 response + 상세페이지를 보여주기 위한 newsId
        JSONObject json = new JSONObject(result);
        if(JSONObject.NULL != json.get("data")){
            for (int i = 0; i < json.getJSONArray("data").length(); i++) {
                JSONObject temp = (JSONObject) json.getJSONArray("data").get(i);
                String newsId = ((JSONObject)json.getJSONArray("data").get(i)).getString("newsId");
                temp.put("detail_url", detail_url + stockCode + "#news/stock/" + newsId);
                jsonarr.put(temp);
            }
        }

        return jsonarr.toString();
    }
}
