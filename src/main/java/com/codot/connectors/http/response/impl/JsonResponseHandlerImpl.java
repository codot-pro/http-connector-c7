package com.codot.connectors.http.response.impl;

import com.codot.connectors.http.outputs.OutputParametersImpl;
import com.codot.connectors.http.response.AbstractResponseHandlerFactory;

import static org.camunda.spin.Spin.S;

public class JsonResponseHandlerImpl extends AbstractResponseHandlerFactory {
    @Override
    public void handle(OutputParametersImpl output, String expectedFileName) {
        output.setResponseType(responseType);
        output.setResponse(S(response));
    }

    @Override
    public void handle(OutputParametersImpl output) {
        handle(output, null);
    }
}
