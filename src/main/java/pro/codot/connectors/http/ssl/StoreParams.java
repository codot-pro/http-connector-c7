package pro.codot.connectors.http.ssl;

import camundajar.impl.com.google.gson.Gson;
import camundajar.impl.com.google.gson.GsonBuilder;
import camundajar.impl.com.google.gson.annotations.Expose;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.file.Paths;

import static pro.codot.connectors.http.HttpConnectorConstants.*;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    @JsonProperty(TLS_STORE_TYPE)
    @Expose private String type;
    @JsonProperty(TLS_STORE_PATH)
    @Expose private String path;
    @JsonProperty(TLS_STORE_PASSWORD)
    @Expose private String password;

    public boolean isEmpty(){
        return path == null && password == null;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
