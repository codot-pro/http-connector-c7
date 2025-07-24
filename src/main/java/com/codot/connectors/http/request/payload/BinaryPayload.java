package com.codot.connectors.http.request.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.camunda.bpm.engine.ProcessEngineException;

import java.io.File;

import static com.codot.connectors.http.HttpConnectorConstants.*;

@Getter
public class BinaryPayload extends AbstractFilePathQualifier implements Payload {
    private final String type;
    private final Boolean delete;

    @JsonCreator
    public BinaryPayload(@JsonProperty(PAYLOAD_KEY_TYPE) String type,
                         @JsonProperty(PAYLOAD_KEY_FILE_NAME) String fileName,
                         @JsonProperty(PAYLOAD_KEY_FILE_PATH) String filePath,
                         @JsonProperty(PAYLOAD_KEY_DELETE) Boolean delete
    ) {
        super(filePath, fileName);
        this.type = type;
        this.delete = delete;
    }
}
