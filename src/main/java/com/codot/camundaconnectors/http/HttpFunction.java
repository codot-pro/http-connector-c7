package com.codot.camundaconnectors.http;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONException;
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

import static org.camunda.spin.Spin.S;

@Component
public class HttpFunction implements JavaDelegate {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpFunction.class);

	String status_code = "";
	String status_msg = "";
	Object response_body = null;
	String response_file_path = "";

	@Override
	public void execute(DelegateExecution delegateExecution) {
		boolean debug = Boolean.parseBoolean((String) delegateExecution.getVariable("debugMode"));

		String url = (String) delegateExecution.getVariable("url");
		int timeout = Integer.parseInt((String) delegateExecution.getVariable("timeout"));
		String payload = (delegateExecution.getVariable("payload")).toString();
		String fileName = (String) delegateExecution.getVariable("response_file_name");

		if (debug)
			startEvent(
					(String) delegateExecution.getVariable("method"),
					url,
					payload,
					(String) delegateExecution.getVariable("headers"),
					fileName,
					delegateExecution
			);

		Map<String, String> headers = null;
		try {
			String headersString = (String) delegateExecution.getVariable("headers");
			if (headersString != null)
				headers = Utility.parseHeaders(headersString);
		} catch (JSONException e){
			status_code = "400";
			status_msg = "Bad request. Invalid headers";
			packRespond(delegateExecution);
			return;
		}

		Method method;
		switch ((String) delegateExecution.getVariable("method")) {
			case "GET": method = Connection.Method.GET; payload = null; break;
			case "POST": method = Connection.Method.POST; break;
			case "PUT": method = Connection.Method.PUT; break;
			default:
				status_code = "400";
				status_msg = "Bad request. Invalid method";
				packRespond(delegateExecution);
				return;
		}

		Connection.Response response;
		try {
			response = Jsoup.connect(url)
					.headers(headers == null ? new HashMap<>() : headers)
					.method(method)
					.requestBody(payload != null ? payload : "")
					.timeout(timeout)
					.ignoreContentType(true)
					.ignoreHttpErrors(true)
					.execute();

			status_code = String.valueOf(response.statusCode());
			status_msg = response.statusMessage();
			byte[] response_bytes = response.bodyAsBytes();
			String response_string = new String(response_bytes, StandardCharsets.UTF_8);
			if (Utility.isValid(response_string)){
				response_body = response_string.isEmpty() ? "" : S(response_string);
			}
			else {
				File file = new File(System.getProperty("java.io.tmpdir"), fileName);
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(response_bytes);
				fos.close();
				response_file_path = file.getAbsolutePath();
				file.deleteOnExit();
			}
		}
		catch (Exception e) {
			status_msg = e.toString();
			LOGGER.error(Utility.printLog(status_msg, delegateExecution));
			if (e.getClass().getSimpleName().equals("SocketTimeoutException")) {
				status_code = "504";
			}
			else {
				status_code = "500";
			}
		}
		if (debug)
			endEvent(delegateExecution);
		packRespond(delegateExecution);
	}

	private void packRespond(DelegateExecution delegateExecution){
		delegateExecution.setVariable("status_code", status_code);
		delegateExecution.setVariable("status_msg", status_msg);
		delegateExecution.setVariable("response_body", response_body == null? "":response_body);
		delegateExecution.setVariable("response_file_path", response_file_path);
	}

	private void startEvent(String method, String url, String payload, String headers,
	                        String fileName, DelegateExecution delegateExecution){
		LOGGER.info(Utility.printLog(
				"{method: " + method + ", URL: " + url + ", payload: " + payload +
						", headers: " + headers  + ", fileName: " + fileName + "}",
				delegateExecution));
	}

	private void endEvent(DelegateExecution delegateExecution){
		LOGGER.info(Utility.printLog("{statusCode: " + status_code + ", statusMsg: "+ status_msg +
				", response_body: " + response_body + ", response_file_path: " + response_file_path + "}",
				delegateExecution));
	}
}
