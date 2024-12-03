package dev.cxl.iam_service.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import dev.cxl.iam_service.service.auth.DefaultServiceImpl;
import dev.cxl.iam_service.service.auth.IAuthService;
import dev.cxl.iam_service.service.auth.KCLServiceImpl;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IdpConfig {
    @Value("${idp.enable}")
    boolean idpEnable;

    private final DefaultServiceImpl idpService;
    private final KCLServiceImpl kclService;

    public IAuthService getAuthService() {
        if (idpEnable) {
            return kclService;
        }
        return idpService;
    }
}
