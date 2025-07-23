package com.codot.connectors.delegates;

import com.codot.connectors.http.inputs.InputParameters;
import com.codot.connectors.http.inputs.InputParametersImpl;
import com.codot.connectors.http.WebClientFactoryProvider;
import com.codot.connectors.utils.Utils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import static com.codot.connectors.http.HttpConnectorConstants.DEBUG_MODE;

@Component
public class HttpConnectorDelegate implements JavaDelegate {
    public static final Logger LOGGER = LoggerFactory.getLogger(HttpConnectorDelegate.class);

    private final WebClientFactoryProvider webClientFactoryProvider;

    public HttpConnectorDelegate(WebClientFactoryProvider webClientFactoryProvider) {
        this.webClientFactoryProvider = webClientFactoryProvider;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        boolean debug = Utils.getBoolean(execution, DEBUG_MODE);

        InputParameters inputParameters = new InputParametersImpl(execution, debug);

        WebClient webClient = webClientFactoryProvider.getClient(inputParameters.getSslProperties());
    }
}
