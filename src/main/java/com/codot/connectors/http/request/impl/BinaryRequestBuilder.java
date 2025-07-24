package com.codot.connectors.http.request.impl;

import com.codot.connectors.http.request.AbstractRequestBuilder;
import org.camunda.bpm.engine.ProcessEngineException;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

import static com.codot.connectors.http.HttpConnectorConstants.METHOD;

public class BinaryRequestBuilder extends AbstractRequestBuilder {

    public BinaryRequestBuilder(WebClient webClient, Properties properties) {
        super(webClient, properties);
    }

    @Override
    public WebClient.RequestHeadersSpec<?> build() {
        WebClient.RequestBodyUriSpec spec = webClient.method(HttpMethod.valueOf(
                properties.getProperty(METHOD, "POST")));

        applyCommonProperties(spec, properties);

        String filename = "";// парсилка пейлоада
        File file = new File(System.getProperty("java.io.tmpdir"), filename);

        byte[] fileAsBytes = null;
        try {
            fileAsBytes = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new ProcessEngineException("File " + filename + " not found");
        }
        return spec.bodyValue(BodyInserters.fromDataBuffers(
                Mono.just(
                        new DefaultDataBufferFactory()
                                .allocateBuffer(fileAsBytes.length)
                                .write(fileAsBytes)
                )));
    }
}
