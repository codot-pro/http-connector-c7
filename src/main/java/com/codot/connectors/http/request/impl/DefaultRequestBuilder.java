package com.codot.connectors.http.request.impl;

import com.codot.connectors.http.request.AbstractRequestBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Properties;
import java.util.Set;

import static com.codot.connectors.http.HttpConnectorConstants.METHOD;

public class DefaultRequestBuilder extends AbstractRequestBuilder {

    public DefaultRequestBuilder(WebClient webClient) {
        super(webClient);
    }

    @Override
    public WebClient.RequestHeadersSpec<?> build(Properties properties) {
        WebClient.RequestBodyUriSpec spec = webClient.method(HttpMethod.valueOf(
                properties.getProperty(METHOD, "GET")));

        applyCommonProperties(spec, properties);

        String method = properties.getProperty("method", "GET").toUpperCase();
        if (Set.of("POST", "PUT", "PATCH").contains(method)) {
            String body = properties.getProperty("payload", "{}");
            return spec.bodyValue(body);
        } else {
            return spec;
        }
    }
}
