package com.codot.camundaconnectors.http;

import com.codot.camundaconnectors.http.tls.StoreParams;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.codot.camundaconnectors.http.ReactorClientHttpConnectorConfig.*;
import static org.camunda.spin.Spin.S;

@Component
public class HttpFunction implements JavaDelegate {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpFunction.class);

	public String status_code = "";
	public String status_msg = "";
	public Object response_body = null;
	public String response_file_path = "";
	MultiValueMap<String, String> responseHeaders = new HttpHeaders();



	@Override
	public void execute(DelegateExecution delegateExecution) {
		//Input Mapping
		HttpMethod method = HttpMethod.valueOf((String) delegateExecution.getVariable("method"));           // Method
		String url = (String) delegateExecution.getVariable("url");                                         // URL
		String jsonHeaders = (String) delegateExecution.getVariable("headers");                             // Headers
		long timeout = Long.parseLong((String) delegateExecution.getVariable("timeout"));                   // Timeout
		String payload = HttpService.getPayloadFromObj(delegateExecution.getVariable("payload"));           // Payload
		String fileName = (String) delegateExecution.getVariable("response_file_name");                     // Response file name
		String attachment = (String) delegateExecution.getVariable("attachment");                           // Attach file
		boolean deleteAttachment = Boolean.parseBoolean((String) delegateExecution.getVariable("delete"));  // Delete attachment?

		// Debug tools
		boolean debug = Boolean.parseBoolean((String) delegateExecution.getVariable("debugMode"));          // Debug mode

		// TLS - Settings
		boolean validateSSL = Boolean.parseBoolean((String) delegateExecution.getVariable("validateSSL"));  // Validate SSL
		boolean is2WaySsl = Boolean.parseBoolean((String) delegateExecution.getVariable("is2WaySsl"));      // Enable Two-way SSL

		if (debug) startEvent(
				method.toString(),
				validateSSL || is2WaySsl, deleteAttachment, url, payload, jsonHeaders,
				fileName, delegateExecution);

		try {
			Map<String, String> headers = new HashMap<>();
			try {
				if (jsonHeaders != null)
					headers = Utility.parseHeaders(jsonHeaders);
			} catch (JSONException e){
				status_code = "400";
				status_msg = "Bad request. Invalid headers";
				packRespond(delegateExecution);
				return;
			}

			String ssl = validateSSL ? (is2WaySsl ? "2waySsl" : "enable") : "disable";
			WebClient client;

            LOGGER.info("SSL: {}", ssl);

			switch (ssl){
				case "enable":
					client = createWebClientWithConnector(getClient());
					break;
				case "disable":
					client = createWebClientWithConnector(getClientWithoutSSL());
					break;
				case "2waySsl":
					StoreParams keyStoreParams = new StoreParams(
							(String) delegateExecution.getVariable("keyStoreType"), // Key Store type
							(String) delegateExecution.getVariable("keyStorePath"), // Key Store path
							(String) delegateExecution.getVariable("keyStorePass")  // Key Store password
					);

					StoreParams trustedStoreParams = new StoreParams(
							(String) delegateExecution.getVariable("trustedStoreType"), // Trusted Store type
							(String) delegateExecution.getVariable("trustedStorePath"), // Trusted Store path
							(String) delegateExecution.getVariable("trustedStorePass")  // Trusted Store password
					);
					// using cacerts IF (path && password) empty
					if (trustedStoreParams.isEmpty()) trustedStoreParams.toDefaultJKS();

					client = createWebClientWithConnector(getClient2WaySSL(keyStoreParams, trustedStoreParams));
					break;
				default:
					status_msg = "Invalid SSL configuration: " + ssl;
					status_code = "500";
					throw new RuntimeException(status_msg);
			}

			Map<String, String> finalHeaders = headers;
			WebClient.RequestBodySpec request = client
					.method(method)
					.uri(url)
					.headers(httpHeaders -> httpHeaders.setAll(finalHeaders));

			Object payloadValue = payload == null ? "{}" : payload;
			if (attachment != null) {
				MultipartBodyBuilder builder = new MultipartBodyBuilder();
				try {
					JSONObject obj = new JSONObject(payload);
					for (Map.Entry<String, Object> key :obj.toMap().entrySet()){
						builder.part(key.getKey(), key.getValue());
					}
				} catch (Exception e){
					LOGGER.error("Error with parsing payload as JSON");
				}

				try {
					File f = new File(System.getProperty("java.io.tmpdir"), attachment);
					builder.part(fileName, new FileSystemResource(f));
				} catch (Exception error){
					status_code = "500";
					status_msg = error.getClass().getSimpleName()+ ": " +error.getMessage();
					LOGGER.error("File for attachment \"{}\" not found", fileName);
				}

				payloadValue = builder.build();
			}

			ByteBuffer res = (HttpService.isBinaryFile(payload) ?
					request.body(HttpService.toBinaryBody(payload, deleteAttachment)) : request.bodyValue(payloadValue))
					.exchangeToMono(clientResponse -> {
						status_code = clientResponse.rawStatusCode() + "";
						responseHeaders.addAll(clientResponse.headers().asHttpHeaders());
						return clientResponse.bodyToMono(ByteBuffer.class);
					})
					.timeout(Duration.ofMillis(timeout))
					.doOnError(error -> {
						status_code = "500";
						status_msg = error.getClass().getSimpleName()+ ": " +error.getMessage();
						LOGGER.error(error.getClass().getSimpleName()+ ": " +error.getMessage(), error);
					})
					.block();

			if (HttpStatus.valueOf(Integer.parseInt(status_code)).is2xxSuccessful() && deleteAttachment)
				if (attachment != null){
					try {
						HttpService.deleteTempFile(attachment);
					} catch (Exception error){
						status_code = "500";
						status_msg = error.getClass().getSimpleName()+ ": " +error.getMessage();
						LOGGER.error("File for attachment \"{}\" not found", fileName);
					}
				} else
					if (HttpService.isBinaryFile(payload)) {
						String binaryFileName = payload.replaceFirst("<<file>>=", "");
						HttpService.deleteTempFile(binaryFileName);
					}

			if (!Objects.isNull(res)){
				byte[] response_bytes = res.array();
				String response_string = new String(response_bytes, StandardCharsets.UTF_8);
				response_body = Utility.valid(response_string);
				if (response_body == null) {
					File file = Files.createTempFile(Utility.getPrefix(fileName), Utility.getSuffix(fileName)).toFile();
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(response_bytes);
					fos.close();
					response_file_path = file.getName();
					if (debug) LOGGER.info(Utility.printLog("File absolute path=" + file.getAbsolutePath(), delegateExecution));
				}
			} else { response_body = "";}
		}
		catch (Exception e) {
			status_msg = e.toString();
			LOGGER.error(Utility.printLog(status_msg, delegateExecution), e);
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
		delegateExecution.setVariable("response_headers", S(responseHeaders, "application/json"));
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
