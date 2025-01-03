package gr.cite.intelcomp.evaluationworkbench.service.keycloak;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(KeycloakResourcesProperties.class)
public class KeycloakResourcesConfiguration {

    private final KeycloakResourcesProperties properties;

    @Autowired
    public KeycloakResourcesConfiguration(KeycloakResourcesProperties properties) {
        this.properties = properties;
    }

    public KeycloakResourcesProperties getProperties() {
        return this.properties;
    }


}
