package dev.cxl.iam_service.respository;

import dev.cxl.iam_service.entity.Role;
import dev.cxl.iam_service.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolePermissionRepository extends JpaRepository<RolePermission,String> {
    List<RolePermission> findByRoleId(String id);

}
