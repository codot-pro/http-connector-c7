package com.codot.connectors.http.request.payload.multipart;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class MultipartFilePart implements MultipartPart {
    private final String type;
    private final String key;
    private final String file;

    @JsonCreator
    public MultipartFilePart(
            @JsonProperty("type") String type,
            @JsonProperty("key") String key,
            @JsonProperty("file") String file
    ) {
        this.type = type;
        this.key = key;
        this.file = file;
    }
}

