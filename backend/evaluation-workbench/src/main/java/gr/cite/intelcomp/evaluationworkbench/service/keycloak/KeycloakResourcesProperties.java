package gr.cite.intelcomp.evaluationworkbench.service.keycloak;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;

@ConfigurationProperties(prefix = "keycloak-resources")
@ConditionalOnProperty(prefix = "keycloak-resources", name = "enabled", havingValue = "true")
public class KeycloakResourcesProperties {

    private HashMap<String, KeycloakAuthorityProperties> authorities;

    public HashMap<String, KeycloakAuthorityProperties> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(HashMap<String, KeycloakAuthorityProperties> authorities) {
        this.authorities = authorities;
    }

}
