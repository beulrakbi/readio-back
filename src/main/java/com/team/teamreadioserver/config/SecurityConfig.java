package com.team.teamreadioserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable()) // CSRF 공격 방어 끄기. 람다식으로 설정
        .authorizeHttpRequests(auth -> auth
          .requestMatchers(
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/v3/api-docs",
            "/webjars/**"
        ).permitAll()
        .anyRequest().permitAll()
    );

    return http.build(); // SecurityFilterChain 객체 반환
  }

}
