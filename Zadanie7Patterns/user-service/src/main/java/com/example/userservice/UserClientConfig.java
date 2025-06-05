package com.example.userservice;

import com.netflix.discovery.AbstractDiscoveryClientOptionalArgs;
import org.springframework.cloud.netflix.eureka.http.WebClientTransportClientFactories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class UserClientConfig {
    @Bean
    public AbstractDiscoveryClientOptionalArgs<?> abstractDiscoveryClientOptionalArgs() {
        return new AbstractDiscoveryClientOptionalArgs<Object>() {};
    }
    @Bean
    public WebClientTransportClientFactories webClientTransportClientFactories() {
        return new WebClientTransportClientFactories(WebClient::builder);
    }
}
