package com.codot.connectors.http.ssl;

import camundajar.impl.com.google.gson.Gson;
import camundajar.impl.com.google.gson.GsonBuilder;
import camundajar.impl.com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Paths;

@Getter
@Setter
@AllArgsConstructor
public class StoreParams {
    private static final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    private static final String OVERRIDDEN_JAVA_TRUST_STORE_PASSWORD = System.getenv("JAVA_TRUST_STORE_PASSWORD");
    private static final String OVERRIDDEN_JAVA_TRUST_STORE_PATH = System.getenv("JAVA_TRUST_STORE_PATH");
    private static final String DEFAULT_JAVA_TRUST_STORE_TYPE = "JKS";
    private static final String DEFAULT_JAVA_TRUST_STORE_PASSWORD = "changeit";
    private static final String DEFAULT_JAVA_TRUST_STORE_PATH = Paths.get(System.getProperty("java.home"), "lib", "security", "cacerts").toAbsolutePath().toString();

    @Getter
    private static final StoreParams defaultStore = new StoreParams(
            DEFAULT_JAVA_TRUST_STORE_TYPE,
            OVERRIDDEN_JAVA_TRUST_STORE_PASSWORD == null ? DEFAULT_JAVA_TRUST_STORE_PASSWORD : OVERRIDDEN_JAVA_TRUST_STORE_PASSWORD,
            OVERRIDDEN_JAVA_TRUST_STORE_PATH == null ? DEFAULT_JAVA_TRUST_STORE_PATH : OVERRIDDEN_JAVA_TRUST_STORE_PATH
    );

    @Expose private String type;
    @Expose private String path;
    @Expose private String pass;

    public boolean isEmpty(){
        return path == null && pass == null;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
