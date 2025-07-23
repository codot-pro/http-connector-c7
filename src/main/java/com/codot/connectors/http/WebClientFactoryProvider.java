package com.codot.connectors.http;

import com.codot.connectors.http.ssl.WebClientFactory;
import com.codot.connectors.http.ssl.impl.NoSSLWebClientFactoryImpl;
import com.codot.connectors.http.ssl.impl.SSLWebClientFactoryImpl;
import com.codot.connectors.http.ssl.impl.TwoWaySSLWebClientFactoryImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Properties;

import static com.codot.connectors.http.HttpConnectorConstants.*;

@Component
public class WebClientFactoryProvider {
    private static final Map<String, WebClientFactory> factories = Map.of(
            SSL_TYPE_DISABLE, new NoSSLWebClientFactoryImpl(),
            SSL_TYPE_ENABLED, new SSLWebClientFactoryImpl(),
            SSL_TYPE_TWO_WAY_SSL, new TwoWaySSLWebClientFactoryImpl()
    );

    public WebClient getClient(Properties sslProperties) {
        WebClientFactory factory = factories.getOrDefault(sslProperties.getProperty(SSL_TYPE), new SSLWebClientFactoryImpl()); // default = enable
        return factory.create(sslProperties);
    }
}
