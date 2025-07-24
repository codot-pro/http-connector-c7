package com.codot.connectors.http.request.payload;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import static com.codot.connectors.http.HttpConnectorConstants.*;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = PAYLOAD_KEY_TYPE,
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextPayload.class, name = PAYLOAD_TYPE_TEXT),
        @JsonSubTypes.Type(value = BinaryPayload.class, name = PAYLOAD_TYPE_BINARY),
        @JsonSubTypes.Type(value = MultipartPayload.class, name = PAYLOAD_TYPE_MULTIPART)
})
public interface Payload {
    String getType();
}
