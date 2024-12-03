package dev.cxl.iam_service.configuration;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;

public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        // Giá trị mặc định nếu không lấy được thông tin người dùng
        String name = "system";

        if (SecurityUtils.getAuthenticatedUserName() != null) {
            return Optional.ofNullable(SecurityUtils.getAuthenticatedUserName());
        }
        // Trả về Optional chứa name hoặc "system"
        return Optional.ofNullable(name);
    }
}
