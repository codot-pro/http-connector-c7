package com.codot.camundaconnectors.http;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.util.HashMap;
import java.util.Map;

import static org.camunda.spin.Spin.S;

class Utility {

	public static String printLog(String msg, DelegateExecution execution) {
		RepositoryService repositoryService = execution.getProcessEngineServices().getRepositoryService();
		ProcessDefinition processDefinition = repositoryService.getProcessDefinition(execution.getProcessDefinitionId());
		String processInstanceId = execution.getProcessInstanceId();
		String source_line = processDefinition.getKey() + ":" + processDefinition.getVersion() + ":" + execution.getCurrentActivityName() + ":" + processInstanceId;
		return " [" + source_line + "]: " + msg;
	}

	public static Map<String, String> parseHeaders(String json) throws  JSONException{
		JSONObject jsonObject;
		if (valid(json) != null){
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

	public static Object valid(String json) {
		if (json.isEmpty()) {
			return json;
		}
		try {
			return S(json);
		} catch (Exception eJson) {
			try {
				return S(XML.toString(XML.toJSONObject(json)));
			} catch (Exception eXml){
				return null;
			}
        }
	}

	static final TrustManager[] TrustManager = new TrustManager[]{
			new X509TrustManager() {
				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				@Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] certs, String authType) {
				}
				@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] certs, String authType) {
				}
			}
	};
	static public String getPrefix(String fileName){
		return fileName.substring(0, fileName.lastIndexOf(".")) + "-";
	}
	static public String getSuffix(String fileName){
		String[] nameParts = fileName.split("\\.");
		return "."+nameParts[nameParts.length - 1];
	}


}
