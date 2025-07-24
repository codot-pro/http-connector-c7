package com.codot.connectors.http.outputs;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.spin.DataFormats;
import org.springframework.util.MultiValueMap;

import static com.codot.connectors.http.HttpConnectorConstants.*;
import static org.camunda.spin.Spin.S;

public class OutputParametersImpl<T> implements OutputParameters{
    int statusCode;
    String responseType;
    T response;
    MultiValueMap<String, String> responseHeaders;

    @Override
    public void save(DelegateExecution e) {
        e.setVariable(OUTPUT_STATUS_CODE, statusCode);
        e.setVariable(OUTPUT_RESPONSE_TYPE, responseType);
        e.setVariable(OUTPUT_RESPONSE, response);
        e.setVariable(OUTPUT_HEADERS, S(responseHeaders, DataFormats.JSON_DATAFORMAT_NAME));
    }
}
