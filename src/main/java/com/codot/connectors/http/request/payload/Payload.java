package com.codot.connectors.http.request.payload;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextPayload.class, name = "text"),
        @JsonSubTypes.Type(value = BinaryPayload.class, name = "binary"),
        @JsonSubTypes.Type(value = MultipartPayload.class, name = "multipart")
})
public interface Payload {
    String getType();
}
