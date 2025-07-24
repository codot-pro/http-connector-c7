package com.codot.connectors.http.request.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class TextPayload implements Payload {
    private final String type;
    private final String text;

    @JsonCreator
    public TextPayload(@JsonProperty("type") String type,
                       @JsonProperty("text") String text) {
        this.type = type;
        this.text = text;
    }
}
