package com.codot.connectors.http.request.impl;

import com.codot.connectors.http.request.AbstractRequestBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Properties;

import static com.codot.connectors.http.HttpConnectorConstants.METHOD;

public class MultipartRequestBuilder extends AbstractRequestBuilder {

    public MultipartRequestBuilder(WebClient webClient) {
        super(webClient);
    }

    @Override
    public WebClient.RequestHeadersSpec<?> build(Properties properties) {
        WebClient.RequestBodyUriSpec spec = webClient.method(HttpMethod.valueOf(
                properties.getProperty(METHOD, "POST")));

        applyCommonProperties(spec, properties);

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

        // Приклад: payload має вигляд "field1=value1;field2=value2"
//        String payload = properties.getProperty("payload", "");
//        for (String pair : payload.split(";")) {
//            String[] kv = pair.split("=", 2);
//            if (kv.length == 2) {
//                bodyBuilder.part(kv[0].trim(), kv[1].trim());
//            }
//        }

        return spec.body(BodyInserters.fromMultipartData(bodyBuilder.build()));
    }
}
