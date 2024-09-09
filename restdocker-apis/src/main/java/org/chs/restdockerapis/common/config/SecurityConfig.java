package org.chs.restdockerapis.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.chs.domain.account.AccountRepository;
import org.chs.restdockerapis.common.jwt.JwtAuthorizationFilter;
import org.chs.tokenissuer.common.properties.JwtProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AccountRepository accountRepository;
    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .httpBasic((httpBasic) -> httpBasic.disable())
                .csrf((csrf) -> csrf.disable())
                .formLogin((formLogin) -> formLogin.disable())
                .headers((header) -> header.frameOptions((frameOptions) -> frameOptions.disable())) // h2 console 에서 사용하는 X-Frame 의 Jacking 방어 => disable
                .sessionManagement((manager) -> manager.sessionCreationPolicy(STATELESS)) // 인증과 인가에 관한 처리를 할 때 Session 을 사용하지 않는다는 의미
                .cors((cors) -> cors.configurationSource(CorsConfigurationSource()))
                .addFilterBefore(new JwtAuthorizationFilter(accountRepository, jwtProperties, objectMapper), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests((authorize) -> authorize.requestMatchers("/api/**", "/*.html").permitAll());


        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource CorsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(Arrays.asList("https://restdocker.site", "https://www.restdocker.site", "http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(), HttpMethod.PATCH.name(), HttpMethod.DELETE.name(), HttpMethod.HEAD.name(), HttpMethod.OPTIONS.name()));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
