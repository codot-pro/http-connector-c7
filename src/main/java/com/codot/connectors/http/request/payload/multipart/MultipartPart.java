package com.codot.connectors.http.request.payload.multipart;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import static com.codot.connectors.http.HttpConnectorConstants.*;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = PAYLOAD_MULTIPART_TYPE,
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MultipartFilePart.class, name = PAYLOAD_MULTIPART_FILE),
        @JsonSubTypes.Type(value = MultipartTextPart.class, name = PAYLOAD_MULTIPART_TEXT)
})
public interface MultipartPart {
    String getKey();
    String getType();
}

