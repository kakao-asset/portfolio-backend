package com.kakaoasset.portfolio.elasticsearch.elasticAPI;

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
//            JSONObject itme = new JSONObject();
//            itme.put("change", temp.getString("change"));
//            itme.put("high52wDate", temp.getString("high52wDate"));
//            itme.put("listingDate", temp.getString("listingDate"));
//            itme.put("low52wDate", temp.getString("low52wDate"));
//            itme.put("name", temp.getString("name"));
//            itme.put("sectorCode", temp.getString("sectorCode"));
//            itme.put("sectorName", temp.getString("sectorName"));
//            itme.put("symbolCode", temp.getString("symbolCode"));
//            itme.put("datetime", temp.getString("datetime"));
//            itme.put("accTradePrice",temp.getLong("accTradePrice"));
//            itme.put("accTradeVolume",temp.getLong("accTradeVolume"));
//            itme.put("bps",temp.getLong("bps"));
//            itme.put("eps",temp.getLong("eps"));
//            itme.put(,temp.getLong());
//            itme.put(,temp.getLong());
//            itme.put(,temp.getLong());
//            itme.put(,temp.getLong());
//            itme.put(,temp.getLong());



            jsonarr.put(temp);
        }

        afterTime = System.currentTimeMillis();
        secDiffTime = (afterTime - beforeTime);
        System.out.println("api "+json.getJSONObject("hits").getJSONArray("hits").length()+" stock data success : " + secDiffTime);
        return jsonarr.toString();
    }

}