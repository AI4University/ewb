package gr.cite.intelcomp.evaluationworkbench.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@EnableConfigurationProperties({EWBClientTMProperties.class, EWBClientClassificationProperties.class})
public class EWBClientConfiguration {

    private final EWBClientTMProperties tmProperties;

    private final EWBClientClassificationProperties classificationProperties;

    public EWBClientConfiguration(EWBClientTMProperties tmProperties, EWBClientClassificationProperties classificationProperties) {
        this.tmProperties = tmProperties;
        this.classificationProperties = classificationProperties;
    }

    @Bean
    @Qualifier("ewbTMClient")
    public WebClient ewbTMClient() {
        ExchangeStrategies strategies = ExchangeStrategies.builder().codecs(
                clientCodecConfigurer ->
                        clientCodecConfigurer.defaultCodecs().maxInMemorySize(tmProperties.getMaxMemory())
        ).build();
        return WebClient.builder().baseUrl(tmProperties.getHostUrl())
                .exchangeStrategies(strategies)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
                .build();
    }

    @Bean
    @Qualifier("ewbClassificationClient")
    public WebClient ewbClassificationClient() {
        ExchangeStrategies strategies = ExchangeStrategies.builder().codecs(
                clientCodecConfigurer ->
                        clientCodecConfigurer.defaultCodecs().maxInMemorySize(classificationProperties.getMaxMemory())
        ).build();
        return WebClient.builder().baseUrl(classificationProperties.getHostUrl())
                .exchangeStrategies(strategies)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
                .build();
    }

}
