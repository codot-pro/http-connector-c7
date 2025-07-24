package com.codot.connectors.http.request.payload.multipart;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MultipartFilePart.class, name = "file"),
        @JsonSubTypes.Type(value = MultipartTextPart.class, name = "text")
})
public interface MultipartPart {
    String getKey();
    String getType();
}

