package com.kakaoasset.portfolio.elasticsearch.elasticAPI.service;


import org.springframework.beans.factory.annotation.Value;
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

@Service
public class RealtimeStockService {

    @Value("${elasticsearch.host}")
    private String host;

    public String selectRealtimeStock(String stock_name){
        String index = "stock-data";
        JSONArray jsonarr = new JSONArray();
        String result = null;

        // make request for elasticsearch api
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            ClientHttpResponse response = execution.execute(request,body);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return response;
        });
        final HttpHeaders headers = new HttpHeaders();
        final HttpEntity<?> entity = new HttpEntity<>(headers);
        try {
            // send request to elasticsearch
            result = restTemplate.exchange("http://"+host+":9200/"+index+"/_search?sort=datetime:acs&size=10000&q=" + stock_name, HttpMethod.GET, entity, String.class).getBody();
        }catch (HttpClientErrorException e){
            // no index
            return new JSONObject("{\"error\":\"No Index\", \"index\":\""+stock_name+"\"}").toString();
        }

        JSONObject json = new JSONObject(result);
        //{"took":31,"timed_out":false,"_shards":{"total":1,"successful":1,"skipped":0,"failed":0},"hits":{"total":{"value":0,"relation":"eq"},"max_score":null,"hits":[]}}
        System.out.println("-------------------------------api-realtime-stock---------------------------");
        System.out.println("len ::: " + json.getJSONObject("hits").getJSONArray("hits").length());
        for (int i = 0; i < json.getJSONObject("hits").getJSONArray("hits").length(); i++) {
            JSONObject temp = ((JSONObject) json.getJSONObject("hits").getJSONArray("hits").get(i)).getJSONObject("_source");
//            System.out.println("temp ::: " + temp.toString());
            jsonarr.put(temp);
        }
        return jsonarr.toString();
    }
}
