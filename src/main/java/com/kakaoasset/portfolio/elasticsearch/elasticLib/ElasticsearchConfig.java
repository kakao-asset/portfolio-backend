package com.kakaoasset.portfolio.elasticsearch.elasticLib;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ElasticsearchConfig {

    private final ElasticsearchProperty property;

    @Bean
    public RestHighLevelClient getRestClient() {

        String host = property.getHost();
        int port = property.getPort();

        HttpHost httpHost = new HttpHost(host, port, "http");


        RestClientBuilder builder = RestClient.builder(httpHost)
                .setRequestConfigCallback(
                        requestConfigBuilder -> requestConfigBuilder
                                .setConnectTimeout(30000)
                                .setSocketTimeout(300000))
                .setHttpClientConfigCallback(
                        httpClientBuilder -> httpClientBuilder
                                .setConnectionReuseStrategy((response, context) -> true)
                                .setKeepAliveStrategy(((response, context) -> 300000))
                                .setDefaultIOReactorConfig(IOReactorConfig.custom()
                                        .setIoThreadCount(4)
                                        .build())

                );

//        return new RestHighLevelClient(builder);

        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(host+":"+port)
                .build();
        return RestClients.create(clientConfiguration).rest();
    }
}