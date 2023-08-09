package com.codot.camundaconnectors.http;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class HttpFunction implements JavaDelegate {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpFunction.class);

	@Override
	public void execute(DelegateExecution delegateExecution) throws Exception {

		String method = (String) delegateExecution.getVariable("method");
		String url = (String) delegateExecution.getVariable("url");
		Map<String, String> headers = parseHeaders((String) delegateExecution.getVariable("headers")); // map
		int timeout = Integer.parseInt((String) delegateExecution.getVariable("timeout"));
		String payload = (String) delegateExecution.getVariable("payload");
		String fileName = (String) delegateExecution.getVariable("response_file_name");

		String status_code = "";
		String status_msg = "";
		String response_body = "";
		String response_file_path = "";

		Method req_method;
		switch (method) {
			case "GET" -> req_method = Connection.Method.GET;
			case "POST" -> req_method = Connection.Method.POST;
			case "PUT" -> req_method = Connection.Method.PUT;
			default -> throw new Exception("req method is wrong"); // todo code + msg
		}

		Connection.Response response;
		try {
			response = Jsoup.connect(url)
					.headers(headers)
					.method(req_method)
					.requestBody(payload)
					.timeout(timeout)
					.ignoreContentType(true)
					.ignoreHttpErrors(true)
					.execute();

			status_code = String.valueOf(response.statusCode());
			status_msg = response.statusMessage();
			byte[] response_bytes = response.bodyAsBytes();
			String response_string = new String(response_bytes, StandardCharsets.UTF_8);
			if (isValid(response_string)){
				response_body = response_string;
			}
			else {
				File file = new File(System.getProperty("java.io.tmpdir"), fileName);
				new FileOutputStream(file).write(response_bytes);
				response_file_path = file.getAbsolutePath();
				file.deleteOnExit();
			}
		}
		catch (Exception e) {
			status_msg = e.toString();
			LOGGER.error(e.toString());
			if (e.getClass().getSimpleName().equals("SocketTimeoutException")) {
				status_code = "504";
			}
			else {
				status_code = "500";
			}
		}
		JSONObject result = new JSONObject("{}");
		result.append("status_code", status_code);
		result.append("status_msg", status_msg);
		result.append("response_body", response_body);
		result.append("response_file_path", response_file_path);
	}

	public static boolean isValid(String json) {
		try {
			new JSONObject(json);
		} catch (JSONException e) {
			return false;
		}
		return true;
	}

	private static Map<String, String> parseHeaders(String json){
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
		throw new JSONException("headers is`t valid");
	}

//	function print_log(msg) {
//		var time_stamp = time_format(new Date(Date.now()));
//
//		var repository_service = execution.getProcessEngineServices().getRepositoryService();
//		var process_definition = repository_service.getProcessDefinition(execution.getProcessDefinitionId());
//		var source_line = process_definition.getKey() + ":" + process_definition.getVersion() + ":" + execution.getCurrentActivityName();
//
//		var log_line = time_stamp + " INFO [" + source_line + "]: " + msg;
//
//		var PrintStream = new java.io.PrintStream(java.lang.System.out, true, "UTF-8");
//		PrintStream.println(log_line);
//
//		return log_line;
//	}
}
