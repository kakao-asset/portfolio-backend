package com.example.kakaoasset.elasticAPI;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
@RestController
public class elasticAPI {

    @RequestMapping("/api")
    public String api(){


        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            ClientHttpResponse response = execution.execute(request,body);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return response;
        });

        final HttpHeaders headers = new HttpHeaders();
        final HttpEntity<?> entity = new HttpEntity<>(headers);

        String res = restTemplate.exchange("http://192.168.0.34:9200/stock-data/_search?pretty&size=10000", HttpMethod.GET, entity, String.class).getBody();

        JSONObject json = new JSONObject(res);

        System.out.println("-------------------------------api---------------------------");

        System.out.println("json = " + json);

        JSONArray jsonarr = new JSONArray();

        for (int i = 0; i < json.getJSONObject("hits").getJSONArray("hits").length(); i++) {
            JSONObject temp = ((JSONObject) json.getJSONObject("hits").getJSONArray("hits").get(i)).getJSONObject("_source");
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

        return jsonarr.toString();
    }

}