package pro.codot.connectors.http.ssl;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.camunda.bpm.engine.ProcessEngineException;
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
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class HttpClientFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientFactory.class);
    private static final ConcurrentHashMap<Integer, ReactorClientHttpConnector> twoWaySslCache = new ConcurrentHashMap<>();

    private static final ConnectionProvider provider = ConnectionProvider.builder("fixed")
            .maxConnections(1000)
            .maxIdleTime(Duration.ofSeconds(20))
            .maxLifeTime(Duration.ofMinutes(5))
            .pendingAcquireTimeout(Duration.ofSeconds(60))
            .evictInBackground(Duration.ofSeconds(120))
            .build();


    public static ReactorClientHttpConnector getClient2WaySSL(StoreParams keyStoreParams, StoreParams trustedStoreParams) {
        int key = 31 * Objects.hashCode(keyStoreParams) + Objects.hashCode(trustedStoreParams);
        return twoWaySslCache.computeIfAbsent(key, k -> buildClient2WaySSL(keyStoreParams, trustedStoreParams));
    }

    private static ReactorClientHttpConnector buildClient2WaySSL(StoreParams keyStoreParams, StoreParams trustedStoreParams) {
        try {
            // KEY Store params
            KeyStore keyStore = KeyStore.getInstance(keyStoreParams.getType());
            if (keyStoreParams.getPassword() == null) throw new ProcessEngineException("Empty Key Store password");
            char[] keyStorePassword = keyStoreParams.getPassword().toCharArray();

            // KEY Store init
            Path storePath = Paths.get(keyStoreParams.getPath());
            keyStore.load(Files.newInputStream(storePath), keyStorePassword);

            // KEY MANAGER init
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keyStorePassword);

            // TRUST Store
            Path trustedPath = Paths.get(trustedStoreParams.getPath());
            if (keyStoreParams.getPassword() == null) throw new ProcessEngineException("Empty Trust Store password");
            char[] trustedStorePassword = trustedStoreParams.getPassword().toCharArray();

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
                    .handshakeTimeoutMillis(10000)
                    .build();

            return new ReactorClientHttpConnector(
                    HttpClient.create(provider)
                            .secure(sslProvider)
            );
        } catch (NoSuchAlgorithmException | KeyStoreException | IOException |
                 UnrecoverableKeyException | java.security.cert.CertificateException e) {
            LOGGER.error("{}: {}", e.getClass().getSimpleName(), e.getMessage(), e);
            throw new ProcessEngineException("Error configuring SSLContext", e);
        } catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            throw new ProcessEngineException("Unknown exception", e);
        }
    }

    static public ReactorClientHttpConnector getClientWithoutSSL(){
        try {
            SslContext finalSslContext = SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
            return new ReactorClientHttpConnector(
                    HttpClient.create(provider)
                            .secure(t -> t.sslContext(finalSslContext))
            );
        } catch (SSLException e) {
            throw new ProcessEngineException(e);
        }
    }

    static public ReactorClientHttpConnector getClient(){
        return new ReactorClientHttpConnector(
                HttpClient.create(provider)
        );
    }
}
