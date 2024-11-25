package dev.cxl.iam_service.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final String[] PUBLIC_ENPOINTS = {
        "/users", "/auth/tfa-first", "/auth/tfa-two", "/auth/introspect", "/auth/logout", "/auth/refresh"
    };
    private final String[] PRIVATE_ENPOINTS = {"/permissions", "/roles"};

    @Value("${jwt.signerKey}")
    private String signerKey;

    @Autowired
    private CustomJWTDecoder customJWTDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.POST, PUBLIC_ENPOINTS)
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/users")
                .hasRole("ADMIN")
                .requestMatchers(PRIVATE_ENPOINTS)
                .hasRole("ADMIN")
                .anyRequest()
                .permitAll());

        httpSecurity.oauth2ResourceServer(
                oauth -> oauth.jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(customJWTDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(
                                new JwtAuthenticationEntryPoint()) // authenticationEntryPoint để bắt các lỗi chưa xác
                // thực
                );
        httpSecurity.csrf(AbstractHttpConfigurer::disable); // tắt csrf để có thể sủ dụng authorizeHttpRequests
        return httpSecurity.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
