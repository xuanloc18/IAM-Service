package dev.cxl.iam_service.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.authentication.jwt")
@Data
public class AuthenticationProperties {
    private String keyStore;
    private String keyStorePassWord;
    private String keyAlias;
}
