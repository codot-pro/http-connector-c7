package com.codot.connectors.http.ssl.impl;

import com.codot.connectors.http.ssl.HttpClientFactory;
import com.codot.connectors.http.ssl.StoreParams;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Properties;

import static com.codot.connectors.http.HttpConnectorConstants.*;

public class TwoWaySSLAbstractWebClientFactory extends AbstractWebClientFactory {
    @Override
    public WebClient create(Properties sslProperties) {
        StoreParams keyStoreParams = new StoreParams(
                getOrThrow(sslProperties, KEY_STORE_TYPE),
                getOrThrow(sslProperties, KEY_STORE_PATH),
                getOrThrow(sslProperties, KEY_STORE_PASS)
        );
        StoreParams trustedStoreParams = new StoreParams(
                sslProperties.getProperty(TRUSTED_STORE_TYPE),
                sslProperties.getProperty(TRUSTED_STORE_PATH),
                sslProperties.getProperty(TRUSTED_STORE_PASS)
        );
        if (trustedStoreParams.isEmpty()) {
            trustedStoreParams = StoreParams.getDefaultStore();
        }

        return WebClient.builder()
                .exchangeStrategies(MEMORY_STRATEGY)
                .clientConnector(HttpClientFactory.getClient2WaySSL(keyStoreParams, trustedStoreParams))
                .build();
    }
}
