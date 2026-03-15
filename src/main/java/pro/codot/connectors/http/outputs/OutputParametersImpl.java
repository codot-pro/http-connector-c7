package pro.codot.connectors.http.outputs;

import camundajar.impl.com.google.gson.Gson;
import camundajar.impl.com.google.gson.GsonBuilder;
import camundajar.impl.com.google.gson.JsonParser;
import camundajar.impl.com.google.gson.annotations.Expose;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import pro.codot.connectors.delegates.HttpConnectorDelegate;
import pro.codot.connectors.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.spin.DataFormats;
import org.camunda.spin.Spin;
import org.camunda.spin.impl.json.jackson.JacksonJsonNode;
import org.springframework.http.HttpHeaders;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static pro.codot.connectors.http.HttpConnectorConstants.*;
import static org.camunda.spin.Spin.S;

@Getter
@Setter
public class OutputParametersImpl implements OutputParameters{
    private static final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    @Expose Integer statusCode;
    @Expose String responseType;
    @Expose Spin<JacksonJsonNode> response;
    @Expose HttpHeaders headers;

    public OutputParametersImpl(){}

    @Override
    public void save(DelegateExecution execution, boolean debug) {
        execution.setVariable(OUTPUT_VARIABLE_STATUS_CODE, statusCode);
        execution.setVariable(OUTPUT_VARIABLE_RESPONSE_TYPE, responseType);
        execution.setVariable(OUTPUT_VARIABLE_RESPONSE, response);
        execution.setVariable(OUTPUT_VARIABLE_HEADERS, S(Optional.ofNullable(headers).orElse(HttpHeaders.EMPTY), DataFormats.JSON_DATAFORMAT_NAME));

        if (debug)
            HttpConnectorDelegate.LOGGER.info(Utils.printLog(this.toString(), execution));
    }

    @Override
    public String toString() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", statusCode);
        map.put("responseType", responseType);
        map.put("response", (response != null) ? response.toString() : null);
        map.put("headers", headers);
        return gson.toJson(map);
    }
}
