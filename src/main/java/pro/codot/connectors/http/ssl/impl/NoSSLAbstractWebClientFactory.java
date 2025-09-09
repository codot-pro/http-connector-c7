package pro.codot.connectors.http.ssl.impl;

import pro.codot.connectors.http.ssl.HttpClientFactory;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Properties;

public class NoSSLAbstractWebClientFactory extends AbstractWebClientFactory {
    @Override
    public WebClient create(Properties sslProperties) {
        return WebClient.builder()
                .exchangeStrategies(MEMORY_STRATEGY)
                .clientConnector(HttpClientFactory.getClientWithoutSSL())
                .build();
    }
}
