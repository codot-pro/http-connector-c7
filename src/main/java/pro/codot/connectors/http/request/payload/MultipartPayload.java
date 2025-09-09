package pro.codot.connectors.http.request.payload;

import pro.codot.connectors.http.request.payload.multipart.MultipartPart;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

import static pro.codot.connectors.http.HttpConnectorConstants.PAYLOAD_KEY_TYPE;
import static pro.codot.connectors.http.HttpConnectorConstants.PAYLOAD_MULTIPART;

@Getter
public class MultipartPayload implements Payload {
    private final String type;
    private final List<MultipartPart> parts;

    @JsonCreator
    public MultipartPayload(
            @JsonProperty(PAYLOAD_KEY_TYPE) String type,
            @JsonProperty(PAYLOAD_MULTIPART) List<MultipartPart> parts) {
        this.type = type;
        this.parts = parts;
    }
}


