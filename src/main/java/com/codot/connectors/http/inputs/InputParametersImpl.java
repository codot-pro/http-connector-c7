package com.codot.connectors.http.inputs;

import camundajar.impl.com.google.gson.Gson;
import camundajar.impl.com.google.gson.GsonBuilder;
import camundajar.impl.com.google.gson.annotations.Expose;
import com.codot.connectors.delegates.HttpConnectorDelegate;
import com.codot.connectors.utils.Utils;
import lombok.Getter;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import java.util.Optional;
import java.util.Properties;

import static com.codot.connectors.http.HttpConnectorConstants.*;

@Getter
public class InputParametersImpl implements InputParameters {
    private static final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    @Expose private final String method;
    @Expose private final String url;
    @Expose private final String headers;
    @Expose private final Integer timeout;
    @Expose private final String payload;
    @Expose private final String responseFileName;
    @Expose private final String sslType;

    @Expose private final String trustedStoreType;
            private final String trustedStorePath;
            private final String trustedStorePass;

    @Expose private final String keyStoreType;
            private final String keyStorePath;
            private final String keyStorePass;

    public InputParametersImpl(DelegateExecution execution, boolean debug) {
        method              = (String) execution.getVariable(METHOD);
        url                 = (String) execution.getVariable(URL);
        timeout             = Integer.parseInt((String) execution.getVariable(TIMEOUT));
        headers             = (String) execution.getVariable(HEADERS);
        payload             = (String) execution.getVariable(PAYLOAD);
        responseFileName    = (String) execution.getVariable(RESPONSE_FILE_NAME);
        sslType             = (String) execution.getVariable(SSL_TYPE);

        trustedStoreType    = (String) execution.getVariable(TRUSTED_STORE_TYPE);
        trustedStorePath    = (String) execution.getVariable(TRUSTED_STORE_PATH);
        trustedStorePass    = (String) execution.getVariable(TRUSTED_STORE_PASS);

        keyStoreType        = (String) execution.getVariable(KEY_STORE_TYPE);
        keyStorePath        = (String) execution.getVariable(KEY_STORE_PATH);
        keyStorePass        = (String) execution.getVariable(KEY_STORE_PASS);

        if (debug) HttpConnectorDelegate.LOGGER.info(Utils.printLog(this.toString(), execution));
    }

    @Override
    public Properties getRequestProperties() {
        Properties properties = new Properties();

        properties.put(METHOD, method);
        properties.put(URL, url);

        Optional.ofNullable(headers).ifPresent(v -> properties.put(HEADERS, v));
        Optional.ofNullable(payload).ifPresent(v -> properties.put(PAYLOAD, v));
        Optional.ofNullable(responseFileName).ifPresent(v -> properties.put(RESPONSE_FILE_NAME, v));

        return properties;
    }

    @Override
    public Properties getSslProperties() {
        Properties properties = new Properties(7);

        properties.put(SSL_TYPE, sslType);

        Optional.ofNullable(keyStoreType).ifPresent(v -> properties.put(KEY_STORE_TYPE, v));
        Optional.ofNullable(keyStorePass).ifPresent(v -> properties.put(KEY_STORE_PASS, v));
        Optional.ofNullable(keyStorePath).ifPresent(v -> properties.put(KEY_STORE_PATH, v));

        Optional.ofNullable(trustedStoreType).ifPresent(v -> properties.put(TRUSTED_STORE_TYPE, v));
        Optional.ofNullable(trustedStorePass).ifPresent(v -> properties.put(TRUSTED_STORE_PASS, v));
        Optional.ofNullable(trustedStorePath).ifPresent(v -> properties.put(TRUSTED_STORE_PATH, v));

        return properties;
    }

    @Override
    public Integer getTimeout(){
        return timeout;
    }

    public String toString() {
        return gson.toJson(this);
    }
}
