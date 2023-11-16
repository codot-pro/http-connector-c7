package com.codot.camundaconnectors.http;

import org.jsoup.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;

public class ConnectionBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionBuilder.class);
    private final Connection connection;
    public ConnectionBuilder(Connection connection) {
        this.connection = connection;
    }

    public Connection build(){
        return connection;
    }
    public void setHeaders(Map<String, String> headers){
        connection.headers(headers);
    }
    public void setMethod(Connection.Method method){
        connection.method(method);
    }
    public void setRequestBody(String payload){
        connection.requestBody(payload);
    }
    public void setData(String attachment, boolean delete) throws IOException {
        File fileToSend = new File(System.getProperty("java.io.tmpdir"), attachment);
        if (fileToSend.exists()) {
            connection.data("file", fileToSend.getName(), Files.newInputStream(fileToSend.toPath()));
            if (delete && !fileToSend.delete()) LOGGER.error("File NOT deleted");
        }
        else {
            LOGGER.error("Attached file with name '" + fileToSend.getName() + "' not exists.");
        }
    }
    public void disableValidateSSL(){
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, Utility.TrustManager, new SecureRandom());

            connection.sslSocketFactory(sc.getSocketFactory());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            LOGGER.error("Error disabling SSL validation (com.codot.camundaconnectors.http.ConnectionBuilder validateSSL method).");
            throw new RuntimeException(e);

        }
    }

}
