package dev.cxl.iam_service.configuration;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        // Giá trị mặc định nếu không lấy được thông tin người dùng
        String name = "system";

        if (SecurityUtils.getAuthenticatedUserName("name") != null) {
            log.info(String.valueOf(Optional.ofNullable(SecurityUtils.getAuthenticatedUserName("name"))));
            return Optional.ofNullable(SecurityUtils.getAuthenticatedUserName("name"));
        }
        // Trả về Optional chứa name hoặc "system"
        return Optional.ofNullable(name);
    }
}
