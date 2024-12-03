package dev.cxl.iam_service.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "security.authentication.jwt")
@Data
public class AuthenticationProperties {
    private String keyStore;
    private String keyStorePassWord;
    private String keyAlias;
}
