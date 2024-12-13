package com.codot.camundaconnectors.http;

import com.codot.camundaconnectors.http.tls.StoreParams;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
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

    private static final ExchangeStrategies MEMORY_STRATEGY = ExchangeStrategies.builder()
            .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(512 * 1024 * 1024))
            .build();

    private static final ConnectionProvider TIME_PROVIDER = ConnectionProvider.builder("default")
            .maxConnections(1000)
            .maxIdleTime(Duration.ofSeconds(20))
            .maxLifeTime(Duration.ofSeconds(60))
            .pendingAcquireTimeout(Duration.ofSeconds(60))
            .evictInBackground(Duration.ofSeconds(120)).build();

    private static final String JKS_CA_PASS = "";//(System.getenv("JKS_CA_PASS") == null ? "changeit" :  System.getenv("JKS_CA_PASS")).toCharArray();
    private static final String JKS_CA_PATH = System.getenv("JKS_CA_PATH");
    private static final Path JKS_CA_FILE = Paths.get(JKS_CA_PATH, "cacerts");

    // https://medium.com/@nazeer.arus18/consuming-a-secure-api-with-mutual-tls-authentication-in-spring-boot-6ad45d7adb92
    public static ReactorClientHttpConnector getClient2WaySSL(StoreParams keyStoreParams, StoreParams trustedStoreParams) {
        ConnectionProvider provider = ConnectionProvider.builder("elastic")
                .maxConnections(1000)
                .maxIdleTime(Duration.ofSeconds(20))
                .maxLifeTime(Duration.ofSeconds(60))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .evictInBackground(Duration.ofSeconds(120)).build();

        try {
            LOGGER.info("getType: {}", keyStoreParams.getType());
            LOGGER.info("getPath: {}", keyStoreParams.getPath());
            LOGGER.info("getPass: {}", keyStoreParams.getPass());

            LOGGER.info("getType: {}", trustedStoreParams.getType());
            LOGGER.info("getPath: {}", trustedStoreParams.getPath());
            LOGGER.info("getPass: {}", trustedStoreParams.getPass());



            // KEY Store params
            KeyStore keyStore = KeyStore.getInstance(keyStoreParams.getType());
            if (keyStoreParams.getPass() == null) throw new RuntimeException("Empty Key Store password");
            char[] keyStorePassword = keyStoreParams.getPass().toCharArray();

            // KEY Store init
            Path storePath = Paths.get(keyStoreParams.getPath());
            keyStore.load(Files.newInputStream(storePath), keyStorePassword);

            // KEY MANAGER init
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keyStorePassword);

            // TRUST Store
            Path trustedPath = Paths.get(trustedStoreParams.getPath());
            if (keyStoreParams.getPass() == null) throw new RuntimeException("Empty Trust Store password");
            char[] trustedStorePassword = trustedStoreParams.getPass().toCharArray();

            // TRUST Store init
            KeyStore trustStore = KeyStore.getInstance(trustedStoreParams.getType());
            trustStore.load(Files.newInputStream(trustedPath), trustedStorePassword);

            // TRUST MANAGER init
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            // SSL builder
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

    static public WebClient createWebClientWithConnector(ReactorClientHttpConnector connector) {
        return WebClient.builder()
                .exchangeStrategies(MEMORY_STRATEGY)
                .clientConnector(connector)
                .build();
    }
}
