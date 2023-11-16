package com.codot.camundaconnectors.http;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.jsoup.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class HttpService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpService.class);

    public Connection.Method getHttpMethod(HttpFunction httpFunction, DelegateExecution delegateExecution, String payload){
        switch ((String) delegateExecution.getVariable("method")) {
            case "GET":
                payload = null;
                return Connection.Method.GET;
            case "POST":
                return Connection.Method.POST;
            case "PUT":
                return Connection.Method.PUT;
            default:
                httpFunction.status_code = "400";
                httpFunction.status_msg = "Bad request. Invalid method";
                httpFunction.packRespond(delegateExecution);
                return null;
        }
    }

    public String getPayloadFromObj(Object payloadObj){
        String payload;
        if (Objects.isNull(payloadObj))	payload = null;
        else {
            payload = payloadObj.toString();
            if (payload.startsWith("\"") && payload.endsWith("\"") && payload.length() > 2){
                payload = payload.substring(1, payload.length() - 1);
            }
        }
        return payload;
    }
}
