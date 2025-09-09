package pro.codot.connectors.http.request;

import org.springframework.web.reactive.function.client.WebClient;

public interface RequestBuilder {
    WebClient.RequestHeadersSpec<?> build();
}
