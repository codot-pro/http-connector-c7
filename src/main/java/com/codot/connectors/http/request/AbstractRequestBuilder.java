package com.codot.connectors.http.request;

import com.codot.connectors.utils.Utils;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Properties;

import static com.codot.connectors.http.HttpConnectorConstants.*;


public abstract class AbstractRequestBuilder implements RequestBuilder {
    protected final WebClient webClient;
    protected final Properties properties;

    protected AbstractRequestBuilder(WebClient webClient, Properties properties) {
        this.webClient = webClient;
        this.properties = properties;
    }

    protected void applyCommonProperties(WebClient.RequestBodyUriSpec spec, Properties props) {
        String uri = props.getProperty(URL);
        WebClient.RequestBodySpec uriSpec = spec.uri(uri);

        String headers = props.getProperty(HEADERS);
        if (headers != null && !headers.isBlank())
            uriSpec.headers(httpHeaders -> httpHeaders.setAll(Utils.parseHeaders(headers)));
    }
}
