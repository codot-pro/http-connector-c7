package pro.codot.connectors.http.request.payload.multipart;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static pro.codot.connectors.http.HttpConnectorConstants.*;

@Getter
public class MultipartTextPart implements MultipartPart {
    private final String type;
    private final String key;
    private final String text;

    @JsonCreator
    public MultipartTextPart(
            @JsonProperty(PAYLOAD_MULTIPART_TYPE) String type,
            @JsonProperty(PAYLOAD_MULTIPART_KEY) String key,
            @JsonProperty(PAYLOAD_MULTIPART_TEXT) String text
    ) {
        this.type = type;
        this.key = key;
        this.text = text;
    }

    @Override
    public String getKey() {
        return key;
    }

}
