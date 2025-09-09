package pro.codot.connectors.http.response;

import pro.codot.connectors.http.response.impl.FileResponseHandlerImpl;
import pro.codot.connectors.http.response.impl.JsonResponseHandlerImpl;
import pro.codot.connectors.http.response.impl.XmlResponseHandlerImpl;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static pro.codot.connectors.http.HttpConnectorConstants.*;

public class ResponseHandlerFactoryProvider {
    private static final Map<String, ResponseHandlerFactory> factories = Map.of(
            OUTPUT_RESPONSE_TYPE_FILE, new FileResponseHandlerImpl(),
            OUTPUT_RESPONSE_TYPE_JSON, new JsonResponseHandlerImpl(),
            OUTPUT_RESPONSE_TYPE_XML, new XmlResponseHandlerImpl()
    );

    public static ResponseHandlerFactory getHandler(ByteBuffer buffer, boolean saveAsFile){
        byte[] bytes = buffer.array();
        String response = new String(bytes, StandardCharsets.UTF_8);
        QualifiedResponseType qualifiedType = saveAsFile ?
                QualifiedResponseType.getFileQualifiedResponseType() :
                QualifiedResponseType.getQualifiedResponseType(response);

        ResponseHandlerFactory factory = factories.getOrDefault(qualifiedType.getType(), new FileResponseHandlerImpl());

        factory.setResponseType(qualifiedType.getType());
        if (qualifiedType.isFile())
            factory.setResponse(buffer);
        else
            factory.setResponse(qualifiedType.getResponse());

        return factory;
    }
}
