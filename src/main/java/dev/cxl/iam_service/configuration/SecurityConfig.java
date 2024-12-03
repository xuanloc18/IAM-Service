package dev.cxl.iam_service.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
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
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final String[] PUBLIC_ENPOINTS_POST = {
        "/users", "/auth/tfa-two", "/auth/introspect", "/auth/logout", "/auth/refresh", "/auth/login"
    };

    private final String[] SWAGGER_ENDPOINT = {
        "/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui/index.html"
    };

    @Value("${idp.enable}")
    Boolean idpEnable;

    @Autowired(required = false)
    CustomJWTDecoder jwtDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.POST, PUBLIC_ENPOINTS_POST)
                .permitAll()
                .requestMatchers(SWAGGER_ENDPOINT)
                .permitAll()
                .anyRequest()
                .authenticated());
        if (idpEnable) {
            httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        } else {
            httpSecurity.oauth2ResourceServer(
                    oauth -> oauth.jwt(jwtConfigurer -> jwtConfigurer
                                    .decoder(jwtDecoder)
                                    .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                            .authenticationEntryPoint(
                                    new JwtAuthenticationEntryPoint()) // authenticationEntryPoint để bắt các lỗi chưa
                    // xác
                    // thực
                    );
        }
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

    @Bean
    CustomPermissionEvaluator customPermissionEvaluator() {
        return new CustomPermissionEvaluator();
    }
    // chỉ ra rằng Spring Security sử dụng CustomPermissionEvaluator khi xử lý các bijou thức như @PreAuthoriz
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(customPermissionEvaluator());
        return expressionHandler;
    }
}
