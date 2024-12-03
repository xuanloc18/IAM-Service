package dev.cxl.iam_service.configuration;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import dev.cxl.iam_service.respository.*;

@Configuration
public class CustomPermissionEvaluator implements PermissionEvaluator {
    @Value("${idp.enable}")
    Boolean idpEnable;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PermissionRespository permissionRespository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        Optional<String> string;
        // đối với mỗi idp thì id trong token khác nhau\
        // check has role admin
        if (idpEnable) {
            string = roleRepository.findRoleNameByUserKCLID(authentication.getName(), "SUPER_ADMIN");
        } else {
            string = roleRepository.findRoleNameByUserID(authentication.getName(), "SUPER_ADMIN");
        }
        if (string.isPresent()) return true;

        // check có permission đối với từng idp
        if (idpEnable) {
            string = permissionRespository.findPermissionIdByUserKCLAndScope(
                    authentication.getName(), targetDomainObject.toString(), permission.toString());
        } else {
            string = permissionRespository.findPermissionIdByUserAndScope(
                    authentication.getName(), targetDomainObject.toString(), permission.toString());
        }
        if (string.isPresent()) return true;

        return false;
    }

    @Override
    public boolean hasPermission(
            Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
