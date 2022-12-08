package com.kakaoasset.portfolio.elasticsearch.elasticAPI.service;

import com.fasterxml.jackson.databind.JsonNode;
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
 * packageName    : com.kakaoasset.portfolio.elasticsearch.elasticAPI.service
 * fileName       : SectorService
 * author         : Hwang
 * date           : 2022-11-14
 */

@Service
public class SectorService {
    @Value("${elasticsearch.host}")
    private String host;

    @Value("${index.multi-stock-index}")
    private String multiIndex;

    public String selectSectorStock(String stock_sector){
        JSONArray jsonarr = new JSONArray();
        String result = null;
        String personResultAsJsonStr = null;

        // make request for elasticsearch api
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            ClientHttpResponse response = execution.execute(request,body);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return response;
        });
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        final HttpEntity<?> entity = new HttpEntity<>(headers);

        String req_body = "{\n" +
                "    \"size\": 0,\n" +
                "    \"query\": {\n" +
                "        \"term\": {\n" +
                "            \"sectorCode.keyword\": {\n" +
                "                \"value\": \""+stock_sector+"\",\n" +
                "                \"boost\": 1.0\n" +
                "            }\n" +
                "        }\n" +
                "    },\n" +
                "    \"_source\": false,\n" +
                "    \"stored_fields\": \"none\",\n" +
                "    \"aggregations\": {\n" +
                "        \"groupby\": {\n" +
                "            \"composite\": {\n" +
                "                \"size\": 1000,\n" +
                "                \"sources\": [\n" +
                "                    {\n" +
                "                        \"ad594295\": {\n" +
                "                            \"terms\": {\n" +
                "                                \"field\": \"name.keyword\",\n" +
                "                                \"missing_bucket\": true,\n" +
                "                                \"order\": \"asc\"\n" +
                "                            }\n" +
                "                        }\n" +
                "                    }\n" +
                "                ]\n" +
                "            },\n" +
                "            \"aggregations\": {\n" +
                "                \"cae43aee\": {\n" +
                "                    \"top_hits\": {\n" +
                "                        \"from\": 0,\n" +
                "                        \"size\": 1,\n" +
                "                        \"version\": false,\n" +
                "                        \"seq_no_primary_term\": false,\n" +
                "                        \"explain\": false,\n" +
                "                        \"docvalue_fields\": [\n" +
                "                            {\n" +
                "                                \"field\": \"datetime.keyword\"\n" +
                "                            }\n" +
                "                        ],\n" +
                "                        \"sort\": [\n" +
                "                            {\n" +
                "                                \"datetime.keyword\": {\n" +
                "                                    \"order\": \"desc\",\n" +
                "                                    \"missing\": \"_last\",\n" +
                "                                    \"unmapped_type\": \"text\"\n" +
                "                                }\n" +
                "                            }\n" +
                "                        ]\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";
        try {
            // send request to elasticsearch

            JSONObject req_json = new JSONObject(req_body);

            HttpEntity<String> request =
                    new HttpEntity<String>(req_json.toString(), headers);

            personResultAsJsonStr =
                    restTemplate.postForObject("http://"+host+":9200/"+multiIndex+"/_search", request, String.class);

        }catch (HttpClientErrorException e){
            // no index
            System.out.println("e = " + e);
            return new JSONObject("{\"error\":\"No Index\", \"sector\":\""+stock_sector+"\"}").toString();
        }

        JSONObject json = new JSONObject(personResultAsJsonStr);

        for (int i = 0; i < json.getJSONObject("aggregations").getJSONObject("groupby").getJSONArray("buckets").length(); i++) {
            JSONObject temp = new JSONObject();
            temp.put("name",((JSONObject) ((JSONObject) json.getJSONObject("aggregations").getJSONObject("groupby").getJSONArray("buckets").get(i)).getJSONObject("cae43aee").getJSONObject("hits").getJSONArray("hits").get(0)).getJSONObject("_source").get("name"));
            temp.put("tradePrice",((JSONObject) ((JSONObject) json.getJSONObject("aggregations").getJSONObject("groupby").getJSONArray("buckets").get(i)).getJSONObject("cae43aee").getJSONObject("hits").getJSONArray("hits").get(0)).getJSONObject("_source").get("tradePrice"));
            temp.put("prevAccTradeVolumeChangeRate", ((JSONObject) ((JSONObject) json.getJSONObject("aggregations").getJSONObject("groupby").getJSONArray("buckets").get(i)).getJSONObject("cae43aee").getJSONObject("hits").getJSONArray("hits").get(0)).getJSONObject("_source").get("prevAccTradeVolumeChangeRate"));
            temp.put("accTradeVolume",  ((JSONObject) ((JSONObject) json.getJSONObject("aggregations").getJSONObject("groupby").getJSONArray("buckets").get(i)).getJSONObject("cae43aee").getJSONObject("hits").getJSONArray("hits").get(0)).getJSONObject("_source").get("accTradeVolume"));
            temp.put("accTradePrice", ((JSONObject) ((JSONObject) json.getJSONObject("aggregations").getJSONObject("groupby").getJSONArray("buckets").get(i)).getJSONObject("cae43aee").getJSONObject("hits").getJSONArray("hits").get(0)).getJSONObject("_source").get("accTradePrice"));
            temp.put("marketCap", ((JSONObject) ((JSONObject) json.getJSONObject("aggregations").getJSONObject("groupby").getJSONArray("buckets").get(i)).getJSONObject("cae43aee").getJSONObject("hits").getJSONArray("hits").get(0)).getJSONObject("_source").get("marketCap"));
            temp.put("foreignRatio", ((JSONObject) ((JSONObject) json.getJSONObject("aggregations").getJSONObject("groupby").getJSONArray("buckets").get(i)).getJSONObject("cae43aee").getJSONObject("hits").getJSONArray("hits").get(0)).getJSONObject("_source").get("foreignRatio"));
            temp.put("id", ((JSONObject) ((JSONObject) json.getJSONObject("aggregations").getJSONObject("groupby").getJSONArray("buckets").get(i)).getJSONObject("cae43aee").getJSONObject("hits").getJSONArray("hits").get(0)).get("_id"));

            jsonarr.put(temp);
        }

        for (int i = 0; i < jsonarr.length(); i++) {
            System.out.println(jsonarr.get(i));
        }

        return jsonarr.toString();
    }

}
