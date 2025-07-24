package com.codot.connectors.http;

import com.codot.connectors.http.request.payload.BinaryPayload;
import com.codot.connectors.http.request.payload.MultipartPayload;
import com.codot.connectors.http.request.payload.Payload;
import com.codot.connectors.http.request.payload.TextPayload;
import com.codot.connectors.http.request.payload.multipart.MultipartFilePart;
import com.codot.connectors.http.request.payload.multipart.MultipartPart;
import com.codot.connectors.http.request.payload.multipart.MultipartTextPart;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import java.util.Properties;

import static com.codot.connectors.http.HttpConnectorConstants.PAYLOAD;
import static com.codot.connectors.http.HttpConnectorConstants.PAYLOAD_TYPE_MULTIPART;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RequestBuilderFactoryTest {
    String multipartPayload = """
        {
           "type": "multipart",
           "parts": [
              {
                 "key": "myFile",
                 "type": "file",
                 "file": "filename.txt"
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
          "file": "path/to/file.bin"
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

        assertEquals(2, mPayload.getParts().size());

        MultipartPart part1 = mPayload.getParts().get(0);
        MultipartPart part2 = mPayload.getParts().get(1);

        assertTrue(part1 instanceof MultipartFilePart);
        assertTrue(part2 instanceof MultipartTextPart);

        MultipartFilePart filePart = (MultipartFilePart) part1;
        MultipartTextPart textPart = (MultipartTextPart) part2;

        assertEquals("myFile", filePart.getKey());
        assertEquals("file", filePart.getType());
        assertEquals("filename.txt", filePart.getFile());

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

        assertEquals("path/to/file.bin", bPayload.getFile());
    }

    @Test
    public void mappingToTextPayloadTest() throws JsonProcessingException {
        Properties properties = new Properties();
        properties.put(PAYLOAD, textPayload);

        Payload payload = RequestBuilderFactory.mappingPayload(properties);

        assertEquals("text", payload.getType());
        assertTrue(payload instanceof TextPayload);

        TextPayload tPayload = (TextPayload) payload;

        assertEquals("{ * content * }", tPayload.getText());
    }

    @Test
    public void mappingToNullTest() throws JsonProcessingException {
        Properties properties = new Properties();

        Payload payload = RequestBuilderFactory.mappingPayload(properties);

        assertEquals("text", payload.getType());
    }
}