package com.codot.connectors.http;

import com.codot.connectors.http.request.payload.BinaryPayload;
import com.codot.connectors.http.request.payload.MultipartPayload;
import com.codot.connectors.http.request.payload.Payload;
import com.codot.connectors.http.request.payload.multipart.MultipartFilePart;
import com.codot.connectors.http.request.payload.multipart.MultipartPart;
import com.codot.connectors.http.request.payload.multipart.MultipartTextPart;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import java.util.Properties;

import static com.codot.connectors.http.HttpConnectorConstants.PAYLOAD;
import static com.codot.connectors.http.HttpConnectorConstants.PAYLOAD_TYPE_MULTIPART;
import static org.junit.Assert.*;

public class RequestBuilderFactoryTest {
    String multipartPayload = """
        {
            "type": "multipart",
            "parts": [
                {
                   "key": "myFile1",
                   "type": "file",
                   "filePath": "path/to/file.txt",
                   "delete": true
                },
                {
                   "key": "myFile2",
                   "type": "file",
                   "fileName": "file.txt",
                   "delete": true
                },
                {
                   "key": "myKey",
                   "type": "text",
                   "text": "{ * content * }"
                }
            ]
        }
        """;
    String binaryPayload = """
        {
          "type": "binary",
          "filePath": "path/to/file.bin"
        }
        """;
    String textPayload = """
        {
          "type": "text",
          "text": "{ * content * }"
        }
        """;

    @Test
    public void mappingToMultipartPayloadTest() throws JsonProcessingException {
        Properties properties = new Properties();
        properties.put(PAYLOAD, multipartPayload);

        Payload payload = RequestBuilderFactory.mappingPayload(properties);

        assertEquals(PAYLOAD_TYPE_MULTIPART, payload.getType());

        MultipartPayload mPayload = (MultipartPayload) payload;

        assertEquals(3, mPayload.getParts().size());

        MultipartPart part1 = mPayload.getParts().get(0);
        MultipartPart part3 = mPayload.getParts().get(2);

        assertTrue(part1 instanceof MultipartFilePart);
        assertTrue(part3 instanceof MultipartTextPart);

        MultipartFilePart filePart = (MultipartFilePart) part1;
        MultipartTextPart textPart = (MultipartTextPart) part3;

        assertEquals("myFile1", filePart.getKey());
        assertEquals("file", filePart.getType());

        assertEquals("myKey", textPart.getKey());
        assertEquals("text", textPart.getType());
        assertEquals("{ * content * }", textPart.getText());
    }

    @Test
    public void mappingToBinaryPayloadTest() throws JsonProcessingException {
        Properties properties = new Properties();
        properties.put(PAYLOAD, binaryPayload);

        Payload payload = RequestBuilderFactory.mappingPayload(properties);

        assertEquals("binary", payload.getType());
        assertTrue(payload instanceof BinaryPayload);

        BinaryPayload bPayload = (BinaryPayload) payload;

        assertEquals("path/to/file.bin", bPayload.getFilePath());
        assertNull(bPayload.getFileName());
    }

    @Test
    public void mappingToTextPayloadTest() {
        Properties properties = new Properties();
        properties.put(PAYLOAD, textPayload);

        assertThrows(JsonProcessingException.class, () -> RequestBuilderFactory.mappingPayload(properties));
    }

    @Test
    public void mappingToEmptyPayloadTest() throws JsonProcessingException {
        Properties properties = new Properties();

        Payload payload = RequestBuilderFactory.mappingPayload(properties);

        assertEquals("empty", payload.getType());
    }
}