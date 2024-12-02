package dev.cxl.iam_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.cxl.iam_service.entity.Permission;
import dev.cxl.iam_service.entity.Role;
import dev.cxl.iam_service.entity.RolePermission;
import dev.cxl.iam_service.exception.AppException;
import dev.cxl.iam_service.exception.ErrorCode;
import dev.cxl.iam_service.respository.PermissionRespository;
import dev.cxl.iam_service.respository.RolePermissionRepository;
import dev.cxl.iam_service.respository.RoleRepository;

@Service
public class RolePermissionService {
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PermissionRespository permissionRespository;

    @Autowired
    RolePermissionRepository rolePermissionRepository;

    public RolePermission create(String roleCode, String perRe, String perCode) {
        Role role = roleRepository.findByCode(roleCode).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        Permission permission = permissionRespository
                .findByResourceCodeAndScope(perRe, perCode)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_EXISTED));
        Boolean check = rolePermissionRepository.findByRoleId(role.getId()).stream()
                .anyMatch(rolePermission -> rolePermission.getPermissionId().equals(permission.getId()));
        if (check) throw new AppException(ErrorCode.ROLE_HAD_PERMISSION);

        return rolePermissionRepository.save(RolePermission.builder()
                .roleId(role.getId())
                .permissionId(permission.getId())
                .deleted(false)
                .build());
    }

    public boolean delete(String id) {
        RolePermission rolePermission = rolePermissionRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_PERMISSION_NOT_EXISTED));
        rolePermission.setDeleted(true);
        rolePermissionRepository.save(rolePermission);
        return true;
    }
}
