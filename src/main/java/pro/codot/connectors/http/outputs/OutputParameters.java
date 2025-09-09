package pro.codot.connectors.http.outputs;

import org.camunda.bpm.engine.delegate.DelegateExecution;

public interface OutputParameters {
    default void save(DelegateExecution e){
        save(e, false);
    }
    void save(DelegateExecution e, boolean debug);
}
