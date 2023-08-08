package com.codot.camundaconnectors.filestorage;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpFunction implements JavaDelegate {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpFunction.class);

	@Override
	public void execute(DelegateExecution delegateExecution) throws Exception {

	}
}
