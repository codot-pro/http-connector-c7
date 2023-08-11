package com.codot.camundaconnectors.http;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

class Utility {

	public static String printLog(String msg, DelegateExecution execution) {
		RepositoryService repositoryService = execution.getProcessEngineServices().getRepositoryService();
		ProcessDefinition processDefinition = repositoryService.getProcessDefinition(execution.getProcessDefinitionId());
		String processInstanceId = execution.getProcessInstanceId();
		String source_line = processDefinition.getKey() + ":" + processDefinition.getVersion() + ":" + execution.getCurrentActivityName() + ":" + processInstanceId;
		return " [" + source_line + "]: " + msg;
	}

	public static byte[] base64Decode(byte[] src){
		return Base64.getDecoder().decode(src);
	}

	public static Map<String, String> parseHeaders(String json){
		JSONObject jsonObject;
		if (isValid(json)){
			jsonObject = new JSONObject(json);
			Map<String, String> transformedMap = new HashMap<>();
			for (Map.Entry<String, Object> entry : jsonObject.toMap().entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue().toString();
				transformedMap.put(key, value);
			}
			return transformedMap;
		}
		throw new JSONException("Bad request. Headers are wrong");
	}

	public static boolean isValid(String json) {
		try {
			new JSONObject(json);
		} catch (JSONException e) {
			return false;
		}
		return true;
	}
}
