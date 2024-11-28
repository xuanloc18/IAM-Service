package dev.cxl.iam_service.configuration;

import dev.cxl.iam_service.service.auth.DefaultServiceImpl;
import dev.cxl.iam_service.service.auth.IAuthService;
import dev.cxl.iam_service.service.auth.KCLServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class idpConfig {
    @Value("${idp.enable}")
    boolean idpEnable;

    private  final DefaultServiceImpl idpService;
    private  final KCLServiceImpl kclService;

    public IAuthService getAuthService() {
        if (idpEnable) {
            return kclService;
        }
        return idpService;
    }


}
