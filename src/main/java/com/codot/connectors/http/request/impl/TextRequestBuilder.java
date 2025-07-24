package com.codot.connectors.http.request.impl;

import com.codot.connectors.http.request.AbstractRequestBuilder;
import com.codot.connectors.http.request.payload.TextPayload;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Properties;

public class TextRequestBuilder extends AbstractRequestBuilder {
    private final TextPayload payload;

    public TextRequestBuilder(WebClient webClient, Properties properties, TextPayload payload) {
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
        return BodyInserters.fromValue(payload.getText());
    }
}
