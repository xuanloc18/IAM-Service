package dev.cxl.iam_service.configuration;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        // Giá trị mặc định nếu không lấy được thông tin người dùng
        String name = "system";

        // Lấy thông tin xác thực hiện tại
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        // Kiểm tra nếu xác thực là dạng AbstractAuthenticationToken
        if (authentication instanceof AbstractAuthenticationToken) {
            // Lấy thông tin principal từ token
            Object principal = ((AbstractAuthenticationToken) authentication).getPrincipal();

            // Nếu principal là JWT, lấy claim "name"
            if (principal instanceof Jwt jwt) {
                name = jwt.getClaim("name");
            } else {
                // Debug nếu principal không phải JWT
                System.out.println("Principal is not a JWT");
            }
        }

        // Trả về Optional chứa name hoặc "system"
        return Optional.ofNullable(name);
    }
}
