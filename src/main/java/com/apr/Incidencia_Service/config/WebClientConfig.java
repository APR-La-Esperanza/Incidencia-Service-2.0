package com.apr.Incidencia_Service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${service.socio.url:http://socio-service}")
    private String socioServiceUrl;

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient webClient(WebClient.Builder loadBalancedWebClientBuilder) {
        if (socioServiceUrl.contains(".") || socioServiceUrl.contains("localhost")) {
            return WebClient.builder().baseUrl(socioServiceUrl).build();
        }
        return loadBalancedWebClientBuilder.baseUrl(socioServiceUrl).build();
    }
}
