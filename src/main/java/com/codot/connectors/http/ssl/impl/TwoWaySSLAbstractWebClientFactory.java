package com.codot.connectors.http.ssl.impl;

import com.codot.connectors.http.ssl.HttpClientFactory;
import com.codot.connectors.http.ssl.StoreConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.ProcessEngineException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Properties;

import static com.codot.connectors.http.HttpConnectorConstants.TLS_SETTINGS;

public class TwoWaySSLAbstractWebClientFactory extends AbstractWebClientFactory {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public WebClient create(Properties sslProperties) {
        String json = sslProperties.getProperty(TLS_SETTINGS);
        StoreConfig config;
        try {
            config = mapper.readValue(json, StoreConfig.class);
        } catch (JsonProcessingException e) {
            throw new ProcessEngineException(e);
        }

        return WebClient.builder()
                .exchangeStrategies(MEMORY_STRATEGY)
                .clientConnector(HttpClientFactory.getClient2WaySSL(config.getKeyStore(), config.getTrustedStore()))
                .build();
    }
}
