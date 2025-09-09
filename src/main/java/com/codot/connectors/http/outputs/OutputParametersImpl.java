package com.codot.connectors.http.outputs;

import camundajar.impl.com.google.gson.Gson;
import camundajar.impl.com.google.gson.GsonBuilder;
import camundajar.impl.com.google.gson.annotations.Expose;
import com.codot.connectors.delegates.HttpConnectorDelegate;
import com.codot.connectors.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.spin.DataFormats;
import org.camunda.spin.Spin;
import org.camunda.spin.impl.json.jackson.JacksonJsonNode;
import org.springframework.http.HttpHeaders;

import static com.codot.connectors.http.HttpConnectorConstants.*;
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

        if (headers != null)
            execution.setVariable(OUTPUT_VARIABLE_HEADERS, S(headers, DataFormats.JSON_DATAFORMAT_NAME));

        if (debug)
            HttpConnectorDelegate.LOGGER.info(Utils.printLog(this.toString(), execution));
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
