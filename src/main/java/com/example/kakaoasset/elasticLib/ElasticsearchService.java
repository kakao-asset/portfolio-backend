package com.example.kakaoasset.elasticLib;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ElasticsearchService {

    private final RestHighLevelClient client;

    private static final String INDEX = "stock-data";

    public SearchResponse sampleQuery() throws IOException {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
//                .query(QueryBuilders.matchAllQuery())
                //  .aggregation() // 필요할 경우 사용
                .size(10000);

        SearchRequest searchRequest = new SearchRequest(INDEX)
                .source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return searchResponse;
    }

}