package pro.codot.connectors.http;

import pro.codot.connectors.http.ssl.HttpClientFactory;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.netty.http.client.HttpClient;
import static org.junit.jupiter.api.Assertions.*;

class WebClientFactoryProviderTest {
    @Test
    void testThatConnectionProviderIsSame() {
        ReactorClientHttpConnector client1 = HttpClientFactory.getClient();
        ReactorClientHttpConnector client2 = HttpClientFactory.getClient();

        HttpClient httpClient1 = (HttpClient) ReflectionTestUtils.getField(client1, "httpClient");
        HttpClient httpClient2 = (HttpClient) ReflectionTestUtils.getField(client2, "httpClient");

        assertSame(httpClient1.configuration().connectionProvider(), httpClient2.configuration().connectionProvider(),
                "Both clients must use the same ConnectionProvider instance");
    }
}