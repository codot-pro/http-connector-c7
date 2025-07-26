package com.codot.connectors.http.ssl;

import org.springframework.web.reactive.function.client.WebClient;

import java.util.Properties;

public interface WebClientFactory {
    WebClient create(Properties sslProperties);
}
