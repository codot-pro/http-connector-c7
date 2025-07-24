package com.codot.connectors.http.request;

import com.codot.connectors.utils.Utils;
import org.json.JSONException;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Properties;


public abstract class AbstractRequestBuilder implements RequestBuilder {
    protected final WebClient webClient;

    protected AbstractRequestBuilder(WebClient webClient) {
        this.webClient = webClient;
    }

    protected void applyCommonProperties(WebClient.RequestBodyUriSpec spec, Properties props) {
        String uri = props.getProperty("url");
        WebClient.RequestBodySpec uriSpec = spec.uri(uri);

        String headers = props.getProperty("headers");
        if (headers != null && !headers.isBlank())
            uriSpec.headers(httpHeaders -> httpHeaders.setAll(Utils.parseHeaders(headers)));
    }
}
