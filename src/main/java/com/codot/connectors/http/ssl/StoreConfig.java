package com.codot.connectors.http.ssl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.codot.connectors.http.HttpConnectorConstants.TLS_KEY_STORE;
import static com.codot.connectors.http.HttpConnectorConstants.TLS_TRUSTED_STORE;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoreConfig {
    @JsonProperty(TLS_KEY_STORE)
    private StoreParams keyStore;

    @JsonProperty(TLS_TRUSTED_STORE)
    private StoreParams trustedStore;

    public StoreParams getTrustedStore() {
        return trustedStore != null ? trustedStore : StoreParams.getDefaultStore();
    }
}
