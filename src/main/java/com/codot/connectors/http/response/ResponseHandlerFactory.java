package com.codot.connectors.http.response;

import com.codot.connectors.http.outputs.OutputParametersImpl;

public interface ResponseHandlerFactory {
    void handle(OutputParametersImpl output, String expectedFileName);
    void handle(OutputParametersImpl output);

    void setResponseType(String responseType);
    void setResponse(Object response);
}
