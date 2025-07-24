package com.codot.connectors.http;

import com.codot.connectors.http.request.RequestBuilder;
import com.codot.connectors.http.request.impl.BinaryRequestBuilder;
import com.codot.connectors.http.request.impl.DefaultRequestBuilder;
import com.codot.connectors.http.request.impl.MultipartRequestBuilder;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Properties;

import static com.codot.connectors.http.HttpConnectorConstants.*;

public class RequestBuilderFactory {
    public static RequestBuilder create(Properties properties, WebClient webClient) {
        String type = properties.getProperty(PAYLOAD_TYPE, PAYLOAD_TYPE_DEFAULT);

        return switch (type.toLowerCase()) {
            case PAYLOAD_TYPE_MULTIPART -> new MultipartRequestBuilder(webClient);
            case PAYLOAD_TYPE_BINARY -> new BinaryRequestBuilder(webClient);
            default -> new DefaultRequestBuilder(webClient);
        };
    }
}
