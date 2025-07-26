package com.codot.connectors.http;

import com.codot.connectors.http.request.RequestBuilder;
import com.codot.connectors.http.request.impl.BinaryRequestBuilder;
import com.codot.connectors.http.request.impl.MultipartRequestBuilder;
import com.codot.connectors.http.request.impl.NoBodyRequestBuilder;
import com.codot.connectors.http.request.impl.TextRequestBuilder;
import com.codot.connectors.http.request.payload.BinaryPayload;
import com.codot.connectors.http.request.payload.EmptyPayload;
import com.codot.connectors.http.request.payload.MultipartPayload;
import com.codot.connectors.http.request.payload.Payload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.ProcessEngineException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Properties;

import static com.codot.connectors.http.HttpConnectorConstants.*;

public class RequestBuilderFactory {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static RequestBuilder create(Properties properties, WebClient webClient) {
        try {
            Payload payload = mappingPayload(properties);

            return switch (payload.getType().toLowerCase()) {
                case PAYLOAD_TYPE_MULTIPART -> new MultipartRequestBuilder(webClient, properties, (MultipartPayload) payload);
                case PAYLOAD_TYPE_BINARY -> new BinaryRequestBuilder(webClient, properties, (BinaryPayload) payload);
                case PAYLOAD_TYPE_EMPTY -> new NoBodyRequestBuilder(webClient, properties);
                default -> throw new ProcessEngineException("Unexpected value: " + payload.getType().toLowerCase());
            };
        } catch (JsonProcessingException e) {
            return new TextRequestBuilder(webClient, properties, properties.getProperty(PAYLOAD));
        }
    }

    public static Payload mappingPayload(Properties properties) throws JsonProcessingException {
        String json = properties.getProperty(PAYLOAD);
        if (json != null)
            return objectMapper.readValue(json, Payload.class);
        return new EmptyPayload();
    }
}
