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

/**
 * packageName    : com.kakaoasset.portfolio.elasticsearch.elasticAPI.service
 * fileName       : SectorService
 * author         : Hwang
 * date           : 2022-11-14
 */

@Service
public class SectorService {
    public String selectSectorStock(String stock_sector){
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

            result = restTemplate.exchange("http://192.168.0.34:9200/"+index+"/_search?sort=datetime:acs&size=10000&q=" + stock_sector, HttpMethod.GET, entity, String.class).getBody();
        }catch (HttpClientErrorException e){
            // no index
            return new JSONObject("{\"error\":\"No Index\", \"sector\":\""+stock_sector+"\"}").toString();
        }

        JSONObject json = new JSONObject(result);

        System.out.println("-------------------------------api-stock-sector--------------------------");

        for (int i = 0; i < json.getJSONObject("hits").getJSONArray("hits").length(); i++) {
            JSONObject temp = ((JSONObject) json.getJSONObject("hits").getJSONArray("hits").get(i)).getJSONObject("_source");
            jsonarr.put(temp);
        }
        return jsonarr.toString();
    }

}
