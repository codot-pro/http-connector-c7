package com.codot.connectors.http.request.payload.multipart;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static com.codot.connectors.http.HttpConnectorConstants.*;

@Getter
public class MultipartFilePart implements MultipartPart {
    private final String type;
    private final String key;
    private final String file;

    @JsonCreator
    public MultipartFilePart(
            @JsonProperty(PAYLOAD_MULTIPART_TYPE) String type,
            @JsonProperty(PAYLOAD_MULTIPART_KEY) String key,
            @JsonProperty(PAYLOAD_MULTIPART_FILE) String file
    ) {
        this.type = type;
        this.key = key;
        this.file = file;
    }
}

