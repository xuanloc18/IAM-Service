package dev.cxl.iam_service.configuration;

import javax.crypto.spec.SecretKeySpec;

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
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final String[] PUBLIC_ENPOINTS = {
        "/users", "/auth/login", "/auth/introspect", "/auth/logout", "/auth/refresh"
    };

    @Value("${jwt.signerKey}")
    private String signerKey;

    @Autowired
    private CustomJWTDecoder customJWTDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.POST, PUBLIC_ENPOINTS)
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/users")
                .hasAuthority("SCOPE_ADMIN") // là admin mới cho vào
                .anyRequest()
                .permitAll());

        httpSecurity.oauth2ResourceServer(
                oauth -> oauth.jwt(jwtConfigurer -> jwtConfigurer.decoder(customJWTDecoder))
                        .authenticationEntryPoint(
                                new JwtAuthenticationEntryPoint()) // authenticationEntryPoint để bắt các lỗi chưa xác
                // thực
                );
        httpSecurity.csrf(AbstractHttpConfigurer::disable); // tắt csrf để có thể sủ dụng authorizeHttpRequests
        return httpSecurity.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512"); // private key
        return NimbusJwtDecoder.withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
