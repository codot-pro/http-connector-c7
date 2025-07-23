package com.codot.connectors.utils;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.repository.ProcessDefinition;

public class Utils {
    public static String printLog(String msg, DelegateExecution execution) {
        RepositoryService repositoryService = execution.getProcessEngineServices().getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(execution.getProcessDefinitionId());
        String processInstanceId = execution.getProcessInstanceId();
        String source_line = processDefinition.getKey() + ":" + processDefinition.getVersion() + ":" + execution.getCurrentActivityName() + ":" + processInstanceId;
        return " [" + source_line + "]: " + msg;
    }

    public static Boolean getBoolean(DelegateExecution e, String key){
        return Boolean.parseBoolean((String) e.getVariable(key));
    }
}
