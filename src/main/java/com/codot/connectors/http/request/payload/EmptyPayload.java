package com.codot.connectors.http.request.payload;

import lombok.Getter;

@Getter
public class EmptyPayload implements Payload{
    private final String type;

    public EmptyPayload() {
        this.type = "empty";
    }
}
