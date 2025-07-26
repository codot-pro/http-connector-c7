package com.codot.connectors.http;

import com.codot.connectors.http.ssl.WebClientFactory;
import com.codot.connectors.http.ssl.impl.NoSSLAbstractWebClientFactory;
import com.codot.connectors.http.ssl.impl.SSLAbstractWebClientFactory;
import com.codot.connectors.http.ssl.impl.TwoWaySSLAbstractWebClientFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Properties;

import static com.codot.connectors.http.HttpConnectorConstants.*;

@Component
public class WebClientFactoryProvider {
    private static final Map<String, WebClientFactory> factories = Map.of(
            SSL_TYPE_DISABLE, new NoSSLAbstractWebClientFactory(),
            SSL_TYPE_ENABLED, new SSLAbstractWebClientFactory(),
            SSL_TYPE_TWO_WAY_SSL, new TwoWaySSLAbstractWebClientFactory()
    );

    public WebClient getClient(Properties sslProperties) {
        WebClientFactory factory = factories.getOrDefault(sslProperties.getProperty(SSL_TYPE), new SSLAbstractWebClientFactory()); // default = enable
        return factory.create(sslProperties);
    }
}
