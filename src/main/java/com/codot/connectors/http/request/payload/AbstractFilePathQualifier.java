package com.codot.connectors.http.request.payload;

import lombok.Getter;
import org.camunda.bpm.engine.ProcessEngineException;

import java.io.File;

@Getter
public abstract class AbstractFilePathQualifier {
    protected final String filePath;
    protected final String fileName;

    protected AbstractFilePathQualifier(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }

    public File getFile() {
        File f = null;
        if (filePath != null)
            f = new File(filePath);
        if (fileName != null)
            f = new File(System.getProperty("java.io.tmpdir"), fileName);
        if (f == null || !f.exists())
            throw new ProcessEngineException("File not found");
        return f;
    }
}
