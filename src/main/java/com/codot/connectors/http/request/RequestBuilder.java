package com.codot.connectors.http.request;

import org.springframework.web.reactive.function.client.WebClient;

import java.util.Properties;

public interface RequestBuilder {
    WebClient.RequestHeadersSpec<?> build(Properties properties);
}
