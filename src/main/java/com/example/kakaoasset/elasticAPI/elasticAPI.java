package com.example.kakaoasset.elasticAPI;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
@RestController
public class elasticAPI {

    @RequestMapping("/api")
    public String api(){

        String index = "stock-data";
        String result = null;
        JSONArray jsonarr = new JSONArray();

        long beforeTime;
        long afterTime;
        double secDiffTime;

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
            beforeTime = System.currentTimeMillis();
            result = restTemplate.exchange("http://192.168.0.34:9200/"+index+"/_search?pretty&size=10000", HttpMethod.GET, entity, String.class).getBody();
        }catch (HttpClientErrorException e){
            // no index
            return new JSONObject("{\"error\":\"No Index\", \"index\":\""+index+"\"}").toString();
        }

        JSONObject json = new JSONObject(result);

        System.out.println("-------------------------------api---------------------------");

        for (int i = 0; i < json.getJSONObject("hits").getJSONArray("hits").length(); i++) {
            JSONObject temp = ((JSONObject) json.getJSONObject("hits").getJSONArray("hits").get(i)).getJSONObject("_source");
            JSONObject itme = new JSONObject();
            itme.put("name", temp.getString("name"));
            itme.put("symbolCode", temp.getString("symbolCode"));
            itme.put("tradePrice", temp.getString("tradePrice"));
            itme.put("timestamp", temp.getString("@timestamp"));
            jsonarr.put(itme);
        }

        afterTime = System.currentTimeMillis();
        secDiffTime = (afterTime - beforeTime);
        System.out.println("api "+json.getJSONObject("hits").getJSONArray("hits").length()+" stock data success : " + secDiffTime + "ms");
        return jsonarr.toString();
    }

}