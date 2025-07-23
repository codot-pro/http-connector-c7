package com.codot.connectors.http.ssl.impl;

import com.codot.connectors.http.ssl.HttpClientFactory;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Properties;

public class NoSSLWebClientFactoryImpl extends WebClientFactoryImpl {
    @Override
    public WebClient create(Properties sslProperties) {
        return WebClient.builder()
                .exchangeStrategies(MEMORY_STRATEGY)
                .clientConnector(HttpClientFactory.getClientWithoutSSL())
                .build();
    }
}
