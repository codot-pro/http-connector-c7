package pro.codot.connectors.http.request.impl;

import pro.codot.connectors.http.request.AbstractRequestBuilder;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Properties;

public class NoBodyRequestBuilder extends AbstractRequestBuilder {
    public NoBodyRequestBuilder(WebClient webClient, Properties properties) {
        super(webClient, properties);
    }

    @Override
    public WebClient.RequestHeadersSpec<?> build() {
        return applyCommonProperties(properties);
    }
}
