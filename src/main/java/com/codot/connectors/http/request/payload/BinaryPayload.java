package com.codot.connectors.http.request.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static com.codot.connectors.http.HttpConnectorConstants.PAYLOAD_KEY_FILE;
import static com.codot.connectors.http.HttpConnectorConstants.PAYLOAD_KEY_TYPE;

@Getter
public class BinaryPayload implements Payload {
    private final String type;
    private final String file;

    @JsonCreator
    public BinaryPayload(@JsonProperty(PAYLOAD_KEY_TYPE) String type,
                         @JsonProperty(PAYLOAD_KEY_FILE) String file) {
        this.type = type;
        this.file = file;
    }
}
