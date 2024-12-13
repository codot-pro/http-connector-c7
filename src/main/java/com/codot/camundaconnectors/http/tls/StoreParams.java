package com.codot.camundaconnectors.http.tls;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Paths;

@Getter
@Setter
@AllArgsConstructor
public class StoreParams {
    private String type;
    private String path;
    private String pass;

    public boolean isEmpty(){
        return path == null && pass == null;
    }

    public void toDefaultJKS(){
        String password = System.getenv("JAVA_TRUST_STORE_PASSWORD");
        String path = System.getenv("JAVA_TRUST_STORE_PATH");

        setPass(password == null ? "changeit" : password);

        setPath(path == null ? Paths.get(System.getProperty("java.home"), "lib", "security", "cacerts").toAbsolutePath().toString() : path);
    }
}
