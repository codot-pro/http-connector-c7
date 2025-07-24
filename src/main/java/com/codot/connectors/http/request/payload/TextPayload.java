package com.codot.connectors.http.request.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static com.codot.connectors.http.HttpConnectorConstants.*;

@Getter
public class TextPayload implements Payload {
    private final String type;
    private final String text;

    @JsonCreator
    public TextPayload(@JsonProperty(PAYLOAD_KEY_TYPE) String type,
                       @JsonProperty(PAYLOAD_KEY_TEXT) String text) {
        this.type = type;
        this.text = text;
    }
}
