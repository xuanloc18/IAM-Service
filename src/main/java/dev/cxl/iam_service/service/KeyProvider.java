package dev.cxl.iam_service.service;

import java.security.KeyPair;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.encrypt.KeyStoreKeyFactory;
import org.springframework.stereotype.Component;

import dev.cxl.iam_service.dto.AuthenticationProperties;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableConfigurationProperties(AuthenticationProperties.class)
@Slf4j
public class KeyProvider implements InitializingBean {
    private final AuthenticationProperties properties;
    private KeyPair keyPair;

    public KeyProvider(AuthenticationProperties properties) {
        this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() {
        this.keyPair = keyPair(properties.getKeyStore(), properties.getKeyStorePassWord(), properties.getKeyAlias());
    }

    private KeyPair keyPair(String keyStore, String password, String alias) {
        KeyStoreKeyFactory keyStoreKeyFactory =
                new KeyStoreKeyFactory(new ClassPathResource(keyStore), password.toCharArray());
        return keyStoreKeyFactory.getKeyPair(alias);
    }

    public KeyPair getKeyPair() {
        return keyPair; // Trả về keyPair đã được khởi tạo trong afterPropertiesSet()
    }
}
