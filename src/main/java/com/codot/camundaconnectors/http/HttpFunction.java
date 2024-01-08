package com.codot.camundaconnectors.http;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class HttpFunction implements JavaDelegate {
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
		long timeout = Long.parseLong((String) delegateExecution.getVariable("timeout"));
		String fileName = (String) delegateExecution.getVariable("response_file_name");
		String attachment = (String) delegateExecution.getVariable("attachment");


		Object payloadObj = delegateExecution.getVariable("payload");
		String payload = HttpService.getPayloadFromObj(payloadObj);

		if (debug) startEvent(
				(String) delegateExecution.getVariable("method"),
				ssl, delete, url, payload, (String) delegateExecution.getVariable("headers"),
				fileName, delegateExecution);


		Map<String, String> headers = new HashMap<>();
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
		try {

			SslContext sslContext = SslContextBuilder
					.forClient()
					.trustManager(InsecureTrustManagerFactory.INSTANCE)
					.build();
			HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

			WebClient client;
			if (!ssl)
				client = WebClient.builder().exchangeStrategies(
						ExchangeStrategies.builder()
								.codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(512 * 1024 * 1024))
								.build()
				).clientConnector(new ReactorClientHttpConnector(httpClient)).build();
			else
				client = WebClient.builder()
						.exchangeStrategies(
								ExchangeStrategies.builder()
										.codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(512 * 1024 * 1024))
										.build()
						).build();


			Map<String, String> finalHeaders = headers;
			WebClient.RequestBodySpec request = client
					.method(HttpMethod.valueOf((String) delegateExecution.getVariable("method")))
					.uri(url)
					.headers(httpHeaders -> httpHeaders.setAll(finalHeaders));

			Object payloadValue = payload;
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
					File f = new File(System.getProperty("java.io.tmpdir"), fileName);
					if (delete) f.deleteOnExit();
					builder.part(fileName, new FileSystemResource(f));
				} catch (Exception error){
					status_code = "500";
					status_msg = error.getClass().getSimpleName()+ ": " +error.getMessage();
					LOGGER.error("File for attachment \"{}\" not found", fileName);
				}


				payloadValue = builder.build();
			}


			ByteBuffer res = (HttpService.isBinaryFile(payload) ?
					request.body(HttpService.toBinaryBody(payload, delete)) : request.bodyValue(payloadValue))
					.exchangeToMono(clientResponse -> {
						status_code = clientResponse.rawStatusCode() + "";
						return clientResponse.bodyToMono(ByteBuffer.class);
					})
					.timeout(Duration.ofMillis(timeout))
					.doOnError(error -> {
						status_code = "500";
						status_msg = error.getClass().getSimpleName()+ ": " +error.getMessage();
						LOGGER.error(error.getClass().getSimpleName()+ ": " +error.getMessage(), error);
					})
					.block();
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
