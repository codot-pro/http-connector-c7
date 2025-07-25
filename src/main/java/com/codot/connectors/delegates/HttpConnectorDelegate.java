package com.codot.connectors.delegates;

import com.codot.connectors.http.RequestBuilderFactory;
import com.codot.connectors.http.WebClientFactoryProvider;
import com.codot.connectors.http.inputs.InputParameters;
import com.codot.connectors.http.inputs.InputParametersImpl;
import com.codot.connectors.http.outputs.OutputParametersImpl;
import com.codot.connectors.http.response.ResponseHandler;
import com.codot.connectors.http.response.ResponseHandlerFactoryProvider;
import com.codot.connectors.utils.Utils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import static com.codot.connectors.http.HttpConnectorConstants.DEBUG_MODE;

@Component
public class HttpConnectorDelegate implements JavaDelegate {
    public static final Logger LOGGER = LoggerFactory.getLogger(HttpConnectorDelegate.class);

    private final WebClientFactoryProvider webClientFactoryProvider;
    private final ResponseHandlerFactoryProvider responseHandlerFactoryProvider;
    private final ResponseHandler responseHandler;

    public HttpConnectorDelegate(WebClientFactoryProvider webClientFactoryProvider, ResponseHandlerFactoryProvider responseHandlerFactoryProvider, ResponseHandler responseHandler) {
        this.webClientFactoryProvider = webClientFactoryProvider;
        this.responseHandlerFactoryProvider = responseHandlerFactoryProvider;
        this.responseHandler = responseHandler;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        boolean debug = Utils.getBoolean(execution, DEBUG_MODE);

        InputParameters inputParameters = new InputParametersImpl(execution, debug);
        OutputParametersImpl outputParameters = new OutputParametersImpl();

        WebClient webClient = webClientFactoryProvider.getClient(inputParameters.getSslProperties());
        WebClient.RequestHeadersSpec<?> request = RequestBuilderFactory
                .create(inputParameters.getRequestProperties(), webClient)
                .build();

        AtomicReference<OutputParametersImpl> output = new AtomicReference<>(outputParameters);

        ByteBuffer body = request
                .exchangeToMono(Mono::just)
                .flatMap(r -> responseHandler.processResponse(r, output.get()))
                .timeout(Duration.ofMillis(inputParameters.getTimeout()))
                .doOnError(error -> responseHandler.handleError(error, output.get()))
                .block();

        if (body != null) {
            responseHandlerFactoryProvider.getHandler(body).handle(outputParameters, inputParameters.getExpectedFileName());
        }
        outputParameters.save(execution);
    }
}
