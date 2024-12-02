package dev.cxl.iam_service.configuration;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import dev.cxl.iam_service.respository.*;

@Configuration
public class CustomPermissionEvaluator implements PermissionEvaluator {
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    RolePermissionRepository rolePermissionRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    PermissionRespository permissionRespository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        // check role admin
        Optional<String> roleCode = roleRepository.findRoleName(authentication.getName(), "SUPER_ADMIN");
        if (roleCode.isPresent()) {
            return true;
        }
        // check has permission
        Optional<String> permissionId = permissionRespository.findPermissionIdByUserAndScope(
                authentication.getName(), targetDomainObject.toString(), permission.toString());
        if (permissionId.isPresent()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean hasPermission(
            Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
