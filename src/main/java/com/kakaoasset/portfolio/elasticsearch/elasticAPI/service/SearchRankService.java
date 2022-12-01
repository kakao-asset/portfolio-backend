package com.kakaoasset.portfolio.elasticsearch.elasticAPI.service;


import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 *packageName    : com.kakaoasset.portfolio.elasticsearch.elasticAPI.service
 * fileName       : SearchRankController
 * author         : Hwang
 * date           : 2022-11-14
*/

@Service
public class SearchRankService {

    @Value("${elasticsearch.host}")
    private String host;

    public String selectSearchRank(){

        String index = "stock-rank";
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

            result = restTemplate.exchange("http://"+host+":9200/"+index+"/_search?sort=datetime:acs", HttpMethod.GET, entity, String.class).getBody();
        }catch (HttpClientErrorException e){
            // no index
            return new JSONObject("{\"error\":\"No Index\"").toString();
        }

        JSONObject json = new JSONObject(result);
        for (int i = 0; i < json.getJSONObject("hits").getJSONArray("hits").length(); i++) {
            JSONObject temp = ((JSONObject) json.getJSONObject("hits").getJSONArray("hits").get(i)).getJSONObject("_source");
            jsonarr.put(temp);
        }
        return jsonarr.toString();
    }
}
