package pro.codot.connectors.http.response;

import lombok.Getter;
import org.camunda.spin.DataFormats;
import org.camunda.spin.Spin;
import org.camunda.spin.impl.json.jackson.JacksonJsonNode;

import static pro.codot.connectors.http.HttpConnectorConstants.*;
import static org.camunda.spin.Spin.S;

@Getter
public class QualifiedResponseType {
    private final Spin<JacksonJsonNode> response;
    private final String type;

    private QualifiedResponseType(Spin<JacksonJsonNode> response, String type) {
        this.response = response;
        this.type = type;
    }

    private QualifiedResponseType(String type) {
        this.response = null;
        this.type = type;
    }

    public static QualifiedResponseType getQualifiedResponseType(String noTyped){
        try {
            Spin<JacksonJsonNode> response = S(noTyped, DataFormats.JSON_DATAFORMAT_NAME);
            return new QualifiedResponseType(response, OUTPUT_RESPONSE_TYPE_JSON);
        } catch (Exception eJson) {
            try {
                Spin<JacksonJsonNode> response = S(noTyped, DataFormats.XML_DATAFORMAT_NAME);
                return new QualifiedResponseType(response, OUTPUT_RESPONSE_TYPE_XML);
            } catch (Exception eXml){
                return new QualifiedResponseType(OUTPUT_RESPONSE_TYPE_FILE);
            }
        }
    }

    public static QualifiedResponseType getFileQualifiedResponseType(){
        return new QualifiedResponseType(OUTPUT_RESPONSE_TYPE_FILE);
    }

    public boolean isFile(){
        return type.equals(OUTPUT_RESPONSE_TYPE_FILE);
    }
}
