package pro.codot.connectors.http.request.impl;

import pro.codot.connectors.http.request.AbstractRequestBuilder;
import pro.codot.connectors.http.request.payload.MultipartPayload;
import pro.codot.connectors.http.request.payload.multipart.MultipartFilePart;
import pro.codot.connectors.http.request.payload.multipart.MultipartPart;
import pro.codot.connectors.http.request.payload.multipart.MultipartTextPart;
import org.camunda.bpm.engine.ProcessEngineException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.util.Properties;

public class MultipartRequestBuilder extends AbstractRequestBuilder {
    private final MultipartPayload payload;

    public MultipartRequestBuilder(WebClient webClient, Properties properties, MultipartPayload payload) {
        super(webClient, properties);
        this.payload = payload;
    }

    @Override
    public WebClient.RequestHeadersSpec<?> build() {
        WebClient.RequestBodySpec spec = applyCommonProperties(properties);

        spec.body(getMultipartBody());

        return spec;
    }

    private BodyInserters.MultipartInserter getMultipartBody(){
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

        for (MultipartPart part : payload.getParts()){
            if (part instanceof MultipartFilePart filePart){
                File f = filePart.getFile();
                if (filePart.getDelete()) f.deleteOnExit();
                bodyBuilder.part(filePart.getKey(), new FileSystemResource(f));
                continue;
            }
            if (part instanceof MultipartTextPart textPart){
                bodyBuilder.part(textPart.getKey(), textPart.getText());
                continue;
            }
            throw new ProcessEngineException("Unexpected value for MultipartPart");
        }

        return BodyInserters.fromMultipartData(bodyBuilder.build());
    }
}
