package com.codot.connectors.http.request.impl;

import com.codot.connectors.http.request.AbstractRequestBuilder;
import com.codot.connectors.http.request.payload.MultipartPayload;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Properties;

public class MultipartRequestBuilder extends AbstractRequestBuilder {
    private final MultipartPayload payload;

    public MultipartRequestBuilder(WebClient webClient, Properties properties, MultipartPayload payload) {
        super(webClient, properties);
        this.payload = payload;
    }

    @Override
    public WebClient.RequestHeadersSpec<?> build() {
        WebClient.RequestBodySpec spec = applyCommonProperties(properties);

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

        // TODO: process each part of payload

        return spec.body(BodyInserters.fromMultipartData(bodyBuilder.build()));
    }
}
