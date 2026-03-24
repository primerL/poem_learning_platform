package edu.fudan.poetryconference.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient() {
        String moonshotApiKey = System.getenv("MOONSHOT_API_KEY");
        if (moonshotApiKey == null || moonshotApiKey.isBlank()) {
            throw new IllegalStateException("MOONSHOT_API_KEY is not configured");
        }
        return WebClient.builder()
                .baseUrl("https://api.moonshot.cn/v1")
                .defaultHeader("Authorization", "Bearer " + moonshotApiKey)
                .build();
    }
}
