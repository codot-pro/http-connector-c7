package com.codot.camundaconnectors.http;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.Spin;
import org.camunda.spin.impl.json.jackson.JacksonJsonNode;
import org.json.JSONException;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

@Component
public class HttpFunction implements JavaDelegate {
	@Autowired
	HttpService service;
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpFunction.class);

	public String status_code = "";
	public String status_msg = "";
	public Object response_body = null;
	public String response_file_path = "";


	@Override
	public void execute(DelegateExecution delegateExecution) {
		boolean debug = Boolean.parseBoolean((String) delegateExecution.getVariable("debugMode"));
		boolean ssl = Boolean.parseBoolean((String) delegateExecution.getVariable("validateSSL"));
		boolean delete = Boolean.parseBoolean((String) delegateExecution.getVariable("delete"));

		String url = (String) delegateExecution.getVariable("url");
		int timeout = Integer.parseInt((String) delegateExecution.getVariable("timeout"));
		String fileName = (String) delegateExecution.getVariable("response_file_name");
		String attachment = (String) delegateExecution.getVariable("attachment");


		Object payloadObj = delegateExecution.getVariable("payload");
		String payload = service.getPayloadFromObj(payloadObj);

		if (debug) startEvent(
				(String) delegateExecution.getVariable("method"),
				ssl, delete, url, payload, (String) delegateExecution.getVariable("headers"),
				fileName, delegateExecution);

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

		Method method = service.getHttpMethod(this, delegateExecution, payload);
		if (method == null) return;

		Connection.Response response;
		try {
			ConnectionBuilder connection = new ConnectionBuilder(Jsoup.connect(url));

			connection.setMethod(method);
			if (headers != null) connection.setHeaders(headers);
			if (payload != null) connection.setRequestBody(payload);
			if (attachment != null) connection.setData(attachment, delete);
			if (!ssl) connection.disableValidateSSL();

			response = connection.build()
					.timeout(timeout)
					.ignoreContentType(true)
					.ignoreHttpErrors(true)
					.maxBodySize(0)
					.execute();

			status_code = String.valueOf(response.statusCode());
			status_msg = response.statusMessage();
			byte[] response_bytes = response.bodyAsBytes();
			String response_string = new String(response_bytes, StandardCharsets.UTF_8);
			if (Utility.isValid(response_string)){
				response_body = Spin.<JacksonJsonNode>S(response_string);
			}
			else {
				File file = Files.createTempFile(Utility.getPrefix(fileName), Utility.getSuffix(fileName)).toFile();
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(response_bytes);
				fos.close();
				response_file_path = file.getName();
				if (debug) LOGGER.info(Utility.printLog("File absolute path=" + file.getAbsolutePath(), delegateExecution));
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

	public void packRespond(DelegateExecution delegateExecution){
		delegateExecution.setVariable("status_code", status_code);
		delegateExecution.setVariable("status_msg", status_msg);
		delegateExecution.setVariable("response_body", response_body == null? "":response_body);
		delegateExecution.setVariable("response_file_path", response_file_path);
	}

	public void startEvent(String method, boolean ssl, boolean delete, String url, String payload, String headers,
						   String fileName, DelegateExecution delegateExecution){
		LOGGER.info(Utility.printLog(
				"{method: " + method + ", URL: " + url + ", payload: " + payload +
						", headers: " + headers  + ", fileName: " + fileName +
						", ssl: " + ssl + ", deleteFile: " + delete +"}",
				delegateExecution));
	}

	public void endEvent(DelegateExecution delegateExecution){
		LOGGER.info(Utility.printLog("{statusCode: " + status_code + ", statusMsg: "+ status_msg +
				", response_body: " + response_body + ", response_file_path: " + response_file_path + "}",
				delegateExecution));
	}
}
