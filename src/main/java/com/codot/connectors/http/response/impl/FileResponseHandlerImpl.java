package com.codot.connectors.http.response.impl;

import com.codot.connectors.http.FileContainer;
import com.codot.connectors.http.outputs.OutputParametersImpl;
import com.codot.connectors.http.response.AbstractResponseHandlerFactory;
import com.codot.connectors.utils.Utils;
import org.camunda.bpm.engine.ProcessEngineException;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;

import static java.util.Map.entry;
import static org.camunda.spin.Spin.S;

public class FileResponseHandlerImpl extends AbstractResponseHandlerFactory {
    private static final Map<MediaType, String> suffixes = Map.ofEntries(
            entry(MediaType.APPLICATION_JSON, "json"),
            entry(MediaType.APPLICATION_PROBLEM_JSON, "json"),
            entry(MediaType.APPLICATION_PDF, "pdf"),
            entry(MediaType.APPLICATION_XML, "xml"),
            entry(MediaType.TEXT_XML, "xml"),
            entry(MediaType.TEXT_PLAIN, "txt"),
            entry(MediaType.TEXT_HTML, "html"),
            entry(MediaType.APPLICATION_XHTML_XML, "xhtml"),
            entry(MediaType.APPLICATION_FORM_URLENCODED, "txt"),
            entry(MediaType.IMAGE_JPEG, "jpg"),
            entry(MediaType.IMAGE_PNG, "png"),
            entry(MediaType.IMAGE_GIF, "gif"),
            entry(MediaType.APPLICATION_OCTET_STREAM, "bin"),
            entry(MediaType.TEXT_MARKDOWN, "md")
    );

    @Override
    public void handle(OutputParametersImpl output, String expectedFileName) {
        ByteBuffer buffer = (ByteBuffer) response;

        String fileName;
        String fileNameFromHeader = output.getHeaders().getContentDisposition().getFilename();

        fileName = Objects.requireNonNullElseGet(
                expectedFileName,
                () -> fileNameFromHeader == null ?
                        "unknown." + qualifyFileSuffix(output.getHeaders().getContentType()):
                        fileNameFromHeader);

        try {
            File file = Files.createTempFile(Utils.getPrefix(fileName), Utils.getSuffix(fileName)).toFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(buffer.array());
            fos.close();

            output.setResponseType(responseType);
            output.setResponse(S(new FileContainer(file).toString()));
        } catch (IOException e) {
            throw new ProcessEngineException(e);
        }
    }

    @Override
    public void handle(OutputParametersImpl output) {
        handle(output, null);
    }

    private String qualifyFileSuffix(MediaType type){
        if (type == null){
            return "temp";
        }

        return suffixes.getOrDefault(MediaType.valueOf(type.getType() + "/" + type.getSubtype()), "temp");
    }
}
