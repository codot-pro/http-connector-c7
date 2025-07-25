package com.codot.connectors.http;

import camundajar.impl.com.google.gson.Gson;
import camundajar.impl.com.google.gson.GsonBuilder;
import camundajar.impl.com.google.gson.annotations.Expose;
import lombok.Getter;
import org.camunda.bpm.engine.ProcessEngineException;

import java.io.File;

@Getter
public class FileContainer {
    private static final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    @Expose protected final String filePath;
    @Expose protected final String fileName;

    public FileContainer(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }

    public FileContainer(File f) {
        fileName = f.getName();
        filePath = f.getAbsolutePath();
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

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
