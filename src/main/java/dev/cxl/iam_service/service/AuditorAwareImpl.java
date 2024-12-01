package dev.cxl.iam_service.service;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        String name = "system"; // Khởi tạo giá trị mặc định
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AbstractAuthenticationToken) {
            Object principal = ((AbstractAuthenticationToken) authentication).getPrincipal();
            if (principal instanceof Jwt jwt) {
                name = jwt.getClaim("name");
            } else {
                System.out.println("Principal is not a JWT");
            }
        }
        return Optional.ofNullable(name); // Trả về Optional
    }

}
