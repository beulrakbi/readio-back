package com.team.teamreadioserver.config;

import com.team.teamreadioserver.user.auth.jwt.JwtAuthenticationFilter;
import com.team.teamreadioserver.user.auth.jwt.JwtTokenProvider;
import com.team.teamreadioserver.user.auth.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;


    public SecurityConfig(JwtTokenProvider tokenProvider,
                          CustomUserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder); // 필수설정~ 비밀번호 비교
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/users/login", "/users/join/**").permitAll()  // 인증 필요없는 경로
                        .requestMatchers("/img/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/user/interests/categories", "/api/user/interests/keywords").permitAll()
                        .requestMatchers("/api/user/**").authenticated()
                        .requestMatchers("/video/**", "/curation/**", "search/**").permitAll()  // 인증 필요없는 경로
                        .requestMatchers(
                                "/",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers("/admin/**").permitAll()// 관리자 관련 경로
//                        .requestMatchers("/admin/**").hasRole("ADMIN") // 관리자 관련 경로
                        .anyRequest().authenticated()   // 그 외는 모두 로그인 필요
                )
                // JwtSecurityConfig 부분이랑 동일한 역할_JwtAuthenticationFilter를 SecurityFilterChain 안에서 등록
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    //cors 설정 추가
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("http://localhost:*");      // 5173이든 5174든 다 허용
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
