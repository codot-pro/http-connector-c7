package pro.codot.connectors.http.response;

import pro.codot.connectors.http.outputs.OutputParametersImpl;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Optional;

import static pro.codot.connectors.http.HttpConnectorConstants.OUTPUT_RESPONSE_TYPE_JSON;
import static org.camunda.spin.Spin.S;

public class ResponseHandler {
    public static Mono<ByteBuffer> processResponse(ClientResponse response, OutputParametersImpl result) {
        result.setStatusCode(response.statusCode().value());
        result.setHeaders(response.headers().asHttpHeaders());

        return response.bodyToMono(ByteBuffer.class);
    }

    public static void handleError(Throwable e, OutputParametersImpl result, Integer statusCode) {
        String msg = Optional.ofNullable(e.getMessage()).orElse("Unknown error");

        result.setResponseType(OUTPUT_RESPONSE_TYPE_JSON);
        result.setResponse(S(toJsonException(e.getClass().getSimpleName() + ": " + msg)));
        result.setStatusCode(statusCode);
    }


    private static String toJsonException(String message){
        return """
                {
                   "error": "%s"
                }
        """.formatted(message);
    }
}
