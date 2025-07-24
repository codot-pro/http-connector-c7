package com.codot.connectors.http.request.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class BinaryPayload implements Payload {
    private final String type;
    private final String file;

    @JsonCreator
    public BinaryPayload(@JsonProperty("type") String type,
                         @JsonProperty("file") String file) {
        this.type = type;
        this.file = file;
    }
}
