package com.codot.camundaconnectors.http;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.SslProvider;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.time.Duration;

public class ReactorClientHttpConnectorConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReactorClientHttpConnectorConfig.class);

    private static final char[] JKS_CA_PASS = (System.getenv("JKS_CA_PASS") == null ? "changeit" :  System.getenv("JKS_CA_PASS")).toCharArray();
    private static final String JKS_CA_PATH = System.getenv("JKS_CA_PATH");
    private static final Path JKS_CA_FILE = Paths.get(JKS_CA_PATH, "cacerts");

    // https://medium.com/@nazeer.arus18/consuming-a-secure-api-with-mutual-tls-authentication-in-spring-boot-6ad45d7adb92
    public static ReactorClientHttpConnector getClient2WaySSL(String PKCS12_FILE_PATH, String PKCS12_PASSWORD, boolean usePKCS12) { // false => JKS, true => PKCS
        ConnectionProvider provider = ConnectionProvider.builder("elastic")
                .maxConnections(1000)
                .maxIdleTime(Duration.ofSeconds(20))
                .maxLifeTime(Duration.ofSeconds(60))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .evictInBackground(Duration.ofSeconds(120)).build();

        try {
            KeyStore keyStore = KeyStore.getInstance(usePKCS12 ? "PKCS12" : "JKS");
            char[] keyStorePassword = (usePKCS12 ? PKCS12_PASSWORD.toCharArray() : JKS_CA_PASS);

            Path storePath = (usePKCS12 ? Paths.get(PKCS12_FILE_PATH) : JKS_CA_FILE);

            keyStore.load(Files.newInputStream(storePath), keyStorePassword);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keyStorePassword);


            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(Files.newInputStream(JKS_CA_FILE), JKS_CA_PASS);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);


            SslContext finalSslContext = SslContextBuilder
                    .forClient()
                    .keyManager(keyManagerFactory)
                    .trustManager(trustManagerFactory)
                    .build();

            SslProvider sslProvider = SslProvider
                    .builder()
                    .sslContext(finalSslContext)
                    .handshakeTimeoutMillis(10000) //higher timeout
                    .build();

            return new ReactorClientHttpConnector(
                    HttpClient.create(provider)
                            .secure(sslProvider)
                            //.option(ChannelOption.SO_KEEPALIVE, true)
//                            .secure(t -> t.sslContext(finalSslContext))
            );
        } catch (NoSuchAlgorithmException | KeyStoreException | IOException |
                 UnrecoverableKeyException | java.security.cert.CertificateException e) {
            LOGGER.info("{}: {}", e.getClass().getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("Error configuring SSLContext", e);
        } catch (Exception e){
            LOGGER.info(e.getMessage(), e);
            throw new RuntimeException("Unknown exception", e);
        }
    }

    static public ReactorClientHttpConnector getClientWithoutSSL(){
        ConnectionProvider provider = ConnectionProvider.builder("fixed")
                .maxConnections(1000)
                .maxIdleTime(Duration.ofSeconds(20))
                .maxLifeTime(Duration.ofSeconds(60))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .evictInBackground(Duration.ofSeconds(120)).build();
        try {
            SslContext finalSslContext = SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
            return new ReactorClientHttpConnector(
                    HttpClient.create(provider)
                            //.option(ChannelOption.SO_KEEPALIVE, true)
                            .secure(t -> t.sslContext(finalSslContext))
            );
        } catch (SSLException e) {
            throw new RuntimeException(e);
        }
    }

    static public ReactorClientHttpConnector getClient(){
        ConnectionProvider provider = ConnectionProvider.builder("fixed")
                .maxConnections(1000)
                .maxIdleTime(Duration.ofSeconds(20))
                .maxLifeTime(Duration.ofSeconds(60))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .evictInBackground(Duration.ofSeconds(120)).build();
        return new ReactorClientHttpConnector(
                HttpClient.create(provider)
                //.option(ChannelOption.SO_KEEPALIVE, true)
        );
    }
}
