package com.team.teamreadioserver.config;

import com.team.teamreadioserver.user.auth.jwt.JwtAuthenticationFilter;
import com.team.teamreadioserver.user.auth.jwt.JwtTokenProvider;
import com.team.teamreadioserver.user.auth.service.CustomUserDetailsService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
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
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers("/img/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
//              .and() (삭제) 람다식에선 필요없음
                .csrf(csrf -> csrf.disable())
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers("/users/login", "/users/join/**", "/video/**", "/curation/**", "/img/**", "/search/**", "/bookPage/**", "/bookReview/**", "/reported/**", "/serviceCenter/**", "/videoBookmark/publicCount/**", "/bookBookmark/publicCount/**").permitAll()  // 인증 필요없는 경로
                        .requestMatchers(HttpMethod.GET, "/api/user/interests/categories", "/api/user/interests/keywords").permitAll()
                                .requestMatchers(HttpMethod.POST, "/serviceCenter/qna/question").authenticated() // 📝 질문 등록
                                .requestMatchers(HttpMethod.PUT, "/serviceCenter/qna/update").authenticated()   // ✏️ 질문 수정
                                .requestMatchers(HttpMethod.DELETE, "/serviceCenter/qna/delete/**").authenticated() // 🗑️ 질문 삭제
                                // /videoBookmark/status/** (개인 북마크 상태 포함)는 인증 필요
                                .requestMatchers("/videoBookmark/status/**").authenticated()
                                .requestMatchers("/bookBookmark/status/**").authenticated()
                                // POST 및 DELETE 요청도 인증 필요
                                .requestMatchers(HttpMethod.POST, "/bookBookmark/**").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/bookBookmark/**").authenticated()
                                .requestMatchers(HttpMethod.POST, "/videoBookmark/**").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/videoBookmark/**").authenticated()
                        .requestMatchers("/api/user/**").authenticated()
                        .requestMatchers(
                                "/",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers("/admin/**","api/admin/").permitAll()       // 관리자 관련 경로(권한 풀고 테스트하면 403뜨는게 정상임 )
//                       .requestMatchers("/admin/**","api/admin/").hasRole("ADMIN")   // 관리자 관련 경로(주석 해제시 해당경로는 관리자로 로그인해야 보임)
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
