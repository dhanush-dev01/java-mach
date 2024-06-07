package org.mach.source.config;

import com.commercetools.api.defaultconfig.ServiceRegion;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.lang.NonNull;

@ConfigurationProperties("commercetools")
public class CTConfiguration {
    private final String projectKey;
    private final String clientId;
    private final String clientSecret;
    private final ServiceRegion serviceRegion;
    private final String apiUrl;

    private final String authTokenUrl;


    @ConstructorBinding
    public CTConfiguration(String projectKey, String clientId, String clientSecret, @NonNull String serviceRegion, String apiUrl, String authTokenUrl) {
        this.projectKey = projectKey;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.serviceRegion = ServiceRegion.valueOf(serviceRegion);
        this.apiUrl = StringUtils.defaultIfBlank(apiUrl, this.serviceRegion.getApiUrl());
        this.authTokenUrl = StringUtils.defaultIfBlank(authTokenUrl, this.serviceRegion.getOAuthTokenUrl());

    }

    public String getProjectKey() {
        return projectKey;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public ServiceRegion getServiceRegion() {
        return serviceRegion;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getAuthTokenUrl() {
        return authTokenUrl;
    }
}
