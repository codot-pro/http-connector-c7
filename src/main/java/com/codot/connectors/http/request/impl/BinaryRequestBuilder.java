package com.codot.connectors.http.request.impl;

import com.codot.connectors.http.request.AbstractRequestBuilder;
import com.codot.connectors.http.request.payload.BinaryPayload;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Properties;

public class BinaryRequestBuilder extends AbstractRequestBuilder {
    private final BinaryPayload payload;

    public BinaryRequestBuilder(WebClient webClient, Properties properties, BinaryPayload payload) {
        super(webClient, properties);
        this.payload = payload;
    }

    @Override
    public WebClient.RequestHeadersSpec<?> build() {
        WebClient.RequestBodySpec spec = applyCommonProperties(properties);

        // TODO: process payload

        return spec;
    }
}
