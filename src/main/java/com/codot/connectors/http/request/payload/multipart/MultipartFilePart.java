package com.codot.connectors.http.request.payload.multipart;

import com.codot.connectors.http.FileContainer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static com.codot.connectors.http.HttpConnectorConstants.*;

@Getter
public class MultipartFilePart extends FileContainer implements MultipartPart {
    private final String type;
    private final String key;
    private final Boolean delete;

    @JsonCreator
    public MultipartFilePart(
            @JsonProperty(PAYLOAD_MULTIPART_TYPE) String type,
            @JsonProperty(PAYLOAD_MULTIPART_KEY) String key,
            @JsonProperty(PAYLOAD_MULTIPART_FILE_NAME) String fileName,
            @JsonProperty(PAYLOAD_MULTIPART_FILE_PATH) String filePath,
            @JsonProperty(PAYLOAD_MULTIPART_DELETE) Boolean delete
    ) {
        super(filePath, fileName);
        this.type = type;
        this.key = key;
        this.delete = delete;
    }
}

