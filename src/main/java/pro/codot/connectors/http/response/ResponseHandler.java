package pro.codot.connectors.http.response;

import pro.codot.connectors.http.outputs.OutputParametersImpl;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

import static pro.codot.connectors.http.HttpConnectorConstants.OUTPUT_RESPONSE_TYPE_JSON;
import static org.camunda.spin.Spin.S;


@Setter
@Getter
@Component
public class ResponseHandler {
    public Mono<ByteBuffer> processResponse(ClientResponse response, OutputParametersImpl result) {
        result.setStatusCode(response.statusCode().value());
        result.setHeaders(response.headers().asHttpHeaders());

        return response.bodyToMono(ByteBuffer.class);
    }

    public void handleError(Throwable error, OutputParametersImpl result) {
        result.setResponseType(OUTPUT_RESPONSE_TYPE_JSON);
        result.setStatusCode(500);
        result.setResponse(S(error.getClass().getSimpleName() + ": " + error.getMessage()));
    }

    public void handleCriticalError(Exception e, OutputParametersImpl result) {
        result.setResponseType(OUTPUT_RESPONSE_TYPE_JSON);
        result.setResponse(S(toJsonException(e.getClass().getSimpleName() + ": " + e.getMessage())));
        result.setStatusCode(e instanceof SocketTimeoutException ? 504 : 500);
    }

    private String toJsonException(String message){
        return """
                {
                   "error": "%s"
                }
        """.formatted(message);
    }
}
