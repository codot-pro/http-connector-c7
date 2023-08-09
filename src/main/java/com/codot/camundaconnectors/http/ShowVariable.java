package com.codot.camundaconnectors.http;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component // Uncomment if you need a debugger
public class ShowVariable implements JavaDelegate {
	private static final Logger LOGGER = LoggerFactory.getLogger(ShowVariable.class);

	@Override
	public void execute(DelegateExecution delegateExecution) throws Exception {
		Map<String, Object> variables = delegateExecution.getVariables();
		LOGGER.info("InstanceId("+delegateExecution.getProcessInstanceId()+") Keys and values:");
		for (Map.Entry<String, Object> entry : variables.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			LOGGER.info(key + ": " + value);
		}
	}
}
