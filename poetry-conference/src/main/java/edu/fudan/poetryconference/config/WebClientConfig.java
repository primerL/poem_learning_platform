package edu.fudan.poetryconference.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://api.moonshot.cn/v1")
                .defaultHeader("Authorization", "Bearer sk-wUMGAQtzjDQBsr1oTQLXNUqyWZW7vTp68qgdML9OzpHnHXsv")
                .build();
    }
}
