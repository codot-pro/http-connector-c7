package pro.codot.connectors.http.ssl.impl;

import pro.codot.connectors.http.ssl.WebClientFactory;
import org.camunda.bpm.engine.ProcessEngineException;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

import java.util.Properties;

public abstract class AbstractWebClientFactory implements WebClientFactory {
    protected final ExchangeStrategies MEMORY_STRATEGY = ExchangeStrategies.builder()
            .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(512 * 1024 * 1024))
            .build();

    protected String getOrThrow(Properties properties, String key){
        String value = properties.getProperty(key);
        if (value == null || value.isEmpty()) {
            throw new ProcessEngineException("Property " + key +" is empty");
        }
        return value;
    }
}
