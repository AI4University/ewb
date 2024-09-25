package gr.cite.intelcomp.evaluationworkbench.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "ewb.client.tm")
public class EWBClientTMProperties {
    private final String hostUrl;
    private final Integer maxMemory;

    @ConstructorBinding
    public EWBClientTMProperties(String hostUrl, Integer maxMemory) {
        this.hostUrl = hostUrl;
        this.maxMemory = maxMemory;
    }

    public String getHostUrl() {
        return hostUrl;
    }

    public Integer getMaxMemory() {
        return maxMemory;
    }
}
