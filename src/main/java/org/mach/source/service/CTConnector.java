package org.mach.source.service;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.client.ByProjectKeyRequestBuilder;
import com.commercetools.api.defaultconfig.ApiRootBuilder;
import org.mach.source.config.CTConfiguration;
import io.vrap.rmf.base.client.oauth2.ClientCredentialsBuilder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CTConnector {
    private final CTConfiguration ctConfig;

    public CTConnector(CTConfiguration config) {
        this.ctConfig = config;
    }

    @Bean
    public ByProjectKeyRequestBuilder getDefaultCTApi() {
        ApiRoot apiRoot = ApiRootBuilder.of().defaultClient(new ClientCredentialsBuilder()
                        .withClientId(ctConfig.getClientId())
                        .withClientSecret(ctConfig.getClientSecret()).build(),
                ctConfig.getAuthTokenUrl(), ctConfig.getApiUrl()).build();
        return apiRoot.withProjectKey(ctConfig.getProjectKey());
    }

}
