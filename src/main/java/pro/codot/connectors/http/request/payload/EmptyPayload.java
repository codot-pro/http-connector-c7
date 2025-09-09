package pro.codot.connectors.http.request.payload;

import lombok.Getter;

import static pro.codot.connectors.http.HttpConnectorConstants.PAYLOAD_TYPE_EMPTY;

@Getter
public class EmptyPayload implements Payload{
    private final String type;

    public EmptyPayload() {
        this.type = PAYLOAD_TYPE_EMPTY;
    }
}
