package com.codot.connectors.http.outputs;

import org.camunda.bpm.engine.delegate.DelegateExecution;

public interface OutputParameters {
    void save(DelegateExecution e);
}
