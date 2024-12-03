package dev.cxl.iam_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.cxl.iam_service.entity.Role;
import dev.cxl.iam_service.entity.User;
import dev.cxl.iam_service.entity.UserRole;
import dev.cxl.iam_service.exception.AppException;
import dev.cxl.iam_service.exception.ErrorCode;
import dev.cxl.iam_service.respository.RoleRepository;
import dev.cxl.iam_service.respository.UserRespository;
import dev.cxl.iam_service.respository.UserRoleRepository;

@Service
public class UserRoleService {
    @Autowired
    UserRespository userRespository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    public UserRole createUserRole(String userMail, String roleCode) {
        User user = userRespository
                .findByUserMail(userMail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Role role = roleRepository.findByCode(roleCode).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        if (role.getDeleted()) {
            throw new AppException(ErrorCode.ROLE_DELETE);
        }
        Boolean check = userRoleRepository.findByUserID(user.getUserID()).stream()
                .anyMatch(userRole -> userRole.getRoleID().equals(role.getId()));

        if (check) throw new AppException(ErrorCode.USER_HAD_ROLE);

        return userRoleRepository.save(UserRole.builder()
                .userID(user.getUserID())
                .roleID(role.getId())
                .deleted(false)
                .build());
    }

    public Boolean delete(String id) {
        UserRole role =
                userRoleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_ROLE_NOT_EXISTED));
        role.setDeleted(true);
        userRoleRepository.save(role);
        return true;
    }

    public Boolean undelete(String id) {
        UserRole role =
                userRoleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_ROLE_NOT_EXISTED));
        role.setDeleted(false);
        userRoleRepository.save(role);
        return true;
    }
}
