package com.codot.connectors.http.request.impl;

import com.codot.connectors.http.request.AbstractRequestBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Properties;
import java.util.Set;

import static com.codot.connectors.http.HttpConnectorConstants.METHOD;

public class TextRequestBuilder extends AbstractRequestBuilder {

    public TextRequestBuilder(WebClient webClient, Properties properties) {
        super(webClient, properties);
    }

    @Override
    public WebClient.RequestHeadersSpec<?> build() {
        WebClient.RequestBodyUriSpec spec = webClient.method(HttpMethod.valueOf(
                properties.getProperty(METHOD, "GET")));

        applyCommonProperties(spec, properties);

        String method = properties.getProperty(METHOD, "GET").toUpperCase();
        if (Set.of("POST", "PUT", "PATCH").contains(method)) {
            String body = properties.getProperty("payload", "{}");
            return spec.bodyValue(body);
        } else {
            return spec;
        }
    }
}
