package com.team.teamreadioserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig { // 수민 사용 !

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
