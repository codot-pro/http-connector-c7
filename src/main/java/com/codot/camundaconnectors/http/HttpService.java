package com.codot.camundaconnectors.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

@Service
public class HttpService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpService.class);

    public static boolean isBinaryFile(String payload){
        if (payload.isEmpty()){
            return false;
        }
        return (payload.startsWith("<<file>>="));
    }

    public static BodyInserter<Mono<DataBuffer>, ReactiveHttpOutputMessage> toBinaryBody(String filename, boolean delete) throws IOException {
        filename = filename.replaceAll("<<file>>=", "");
        File f = new File(System.getProperty("java.io.tmpdir"), filename);
        if (delete) f.deleteOnExit();
        byte[] fAsBytes = Files.readAllBytes(f.toPath());
        return BodyInserters.fromDataBuffers(
                Mono.just(
                        new DefaultDataBufferFactory()
                                .allocateBuffer(fAsBytes.length)
                                .write(fAsBytes)
                )
        );
    }

    public static String getPayloadFromObj(Object payloadObj){
        String payload;
        if (Objects.isNull(payloadObj))	payload = null;
        else {
            payload = payloadObj.toString();
            if (payload.startsWith("\"") && payload.endsWith("\"") && payload.length() > 2){
                payload = payload.substring(1, payload.length() - 1);
            }
        }
        return payload;
    }
}
