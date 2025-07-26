package com.codot.connectors.http.request.impl;

import com.codot.connectors.http.request.AbstractRequestBuilder;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Properties;

public class TextRequestBuilder extends AbstractRequestBuilder {
    private final String payload;

    public TextRequestBuilder(WebClient webClient, Properties properties, String payload) {
        super(webClient, properties);
        this.payload = payload;
    }

    @Override
    public WebClient.RequestHeadersSpec<?> build() {
        WebClient.RequestBodySpec spec = applyCommonProperties(properties);

        spec.body(getTextBody());

        return spec;
    }

    BodyInserter<String, ReactiveHttpOutputMessage> getTextBody(){
        System.out.println(payload);
        return BodyInserters.fromValue(payload);
    }
}
