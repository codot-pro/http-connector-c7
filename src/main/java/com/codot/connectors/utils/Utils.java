package com.codot.connectors.utils;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.camunda.spin.Spin.S;

public class Utils {
    public static String printLog(String msg, DelegateExecution execution) {
        RepositoryService repositoryService = execution.getProcessEngineServices().getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(execution.getProcessDefinitionId());
        String processInstanceId = execution.getProcessInstanceId();
        String source_line = processDefinition.getKey() + ":" + processDefinition.getVersion() + ":" + execution.getCurrentActivityName() + ":" + processInstanceId;
        return " [" + source_line + "]: " + msg;
    }

    public static Map<String, String> parseHeaders(String json) {
        try {
            JSONObject headers = new JSONObject(json);

            Map<String, String> transformedMap = new HashMap<>();
            Iterator<String> headersIterator = headers.keys();

            while(headersIterator.hasNext()){
                String key = headersIterator.next();

                transformedMap.put(key, headers.getString(key));
            }
            return transformedMap;
        } catch (JSONException e) {
            throw new ProcessEngineException(e);
        }
    }

    public static Boolean getBoolean(DelegateExecution e, String key){
        return Boolean.parseBoolean((String) e.getVariable(key));
    }
}
