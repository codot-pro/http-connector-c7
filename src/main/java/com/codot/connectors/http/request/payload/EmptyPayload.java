package com.codot.connectors.http.request.payload;

import lombok.Getter;

import static com.codot.connectors.http.HttpConnectorConstants.PAYLOAD_TYPE_EMPTY;

@Getter
public class EmptyPayload implements Payload{
    private final String type;

    public EmptyPayload() {
        this.type = PAYLOAD_TYPE_EMPTY;
    }
}
