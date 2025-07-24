package com.codot.connectors.http.request.impl;

import com.codot.connectors.http.request.AbstractRequestBuilder;
import com.codot.connectors.http.request.payload.BinaryPayload;
import org.camunda.bpm.engine.ProcessEngineException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

public class BinaryRequestBuilder extends AbstractRequestBuilder {
    private final BinaryPayload payload;

    public BinaryRequestBuilder(WebClient webClient, Properties properties, BinaryPayload payload) {
        super(webClient, properties);
        this.payload = payload;
    }

    @Override
    public WebClient.RequestHeadersSpec<?> build() {
        WebClient.RequestBodySpec spec = applyCommonProperties(properties);

        try {
            spec.body(getBinaryBody());
        } catch (IOException e) {
            throw new ProcessEngineException(e);
        }

        return spec;
    }

    private BodyInserter<Mono<DataBuffer>, ReactiveHttpOutputMessage> getBinaryBody() throws IOException {
        File f = payload.getFile();
        if (payload.getDelete()) f.deleteOnExit();
        byte[] fAsBytes = Files.readAllBytes(f.toPath());
        return BodyInserters.fromDataBuffers(
                Mono.just(
                        new DefaultDataBufferFactory()
                                .allocateBuffer(fAsBytes.length)
                                .write(fAsBytes)
                )
        );
    }
}
