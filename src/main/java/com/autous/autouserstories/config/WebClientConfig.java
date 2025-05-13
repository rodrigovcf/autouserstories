package com.autous.autouserstories.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient lab45WebClient(Dotenv dotenv) {
        String apiToken = dotenv.get("LAB45_API_TOKEN");
        if (apiToken == null || apiToken.isEmpty()) {
            throw new IllegalArgumentException("LAB45_API_TOKEN is not defined in .env");
        }
        return WebClient.builder()
                .baseUrl("https://api.lab45.ai/v1.1")
                .defaultHeader("Authorization", "Bearer " + apiToken)
                .build();
    }
}