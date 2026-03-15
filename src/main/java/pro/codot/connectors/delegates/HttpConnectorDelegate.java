package pro.codot.connectors.delegates;

import lombok.NoArgsConstructor;
import pro.codot.connectors.http.RequestBuilderFactory;
import pro.codot.connectors.http.WebClientFactoryProvider;
import pro.codot.connectors.http.inputs.InputParameters;
import pro.codot.connectors.http.inputs.InputParametersImpl;
import pro.codot.connectors.http.outputs.OutputParametersImpl;
import pro.codot.connectors.http.response.ResponseHandler;
import pro.codot.connectors.http.response.ResponseHandlerFactoryProvider;
import pro.codot.connectors.utils.Utils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Exceptions;

import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import static pro.codot.connectors.http.HttpConnectorConstants.DEBUG_MODE;

@NoArgsConstructor
public class HttpConnectorDelegate implements JavaDelegate {
    public static final Logger LOGGER = LoggerFactory.getLogger(HttpConnectorDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        boolean debug = Utils.getBoolean(execution, DEBUG_MODE);

        InputParameters inputParameters = new InputParametersImpl(execution, debug);
        OutputParametersImpl outputParameters = new OutputParametersImpl();

        boolean saveAsFile = inputParameters.shouldSaveAsFile();

        WebClient webClient = WebClientFactoryProvider.getClient(inputParameters.getSslProperties());
        WebClient.RequestHeadersSpec<?> request = RequestBuilderFactory
                .create(inputParameters.getRequestProperties(), webClient)
                .build();

        AtomicReference<OutputParametersImpl> output = new AtomicReference<>(outputParameters);

        try {
            ByteBuffer body = request
                    .exchangeToMono(r -> ResponseHandler.processResponse(r, output.get()))
                    .timeout(Duration.ofMillis(inputParameters.getTimeout()))
                    .block();
            if (body != null) {
                ResponseHandlerFactoryProvider.getHandler(body, saveAsFile).handle(outputParameters, inputParameters.getExpectedFileName());
            }
        } catch (Throwable e) {
            Throwable cause = Exceptions.unwrap(e);

            if (cause instanceof TimeoutException
                    || cause instanceof SocketTimeoutException) {
                ResponseHandler.handleError(cause, output.get(), 504);
            } else {
                ResponseHandler.handleError(cause, output.get(), 500);
            }
        } finally {
            outputParameters.save(execution, debug);
        }
    }
}
