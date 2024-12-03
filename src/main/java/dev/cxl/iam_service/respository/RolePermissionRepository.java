package dev.cxl.iam_service.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.cxl.iam_service.entity.RolePermission;

public interface RolePermissionRepository extends JpaRepository<RolePermission, String> {
    List<RolePermission> findByRoleId(String id);
}
