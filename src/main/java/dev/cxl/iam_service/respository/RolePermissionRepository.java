package dev.cxl.iam_service.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.cxl.iam_service.entity.RolePermission;

public interface RolePermissionRepository extends JpaRepository<RolePermission, String> {
    List<RolePermission> findByRoleId(String id);

    @Query("SELECT rp FROM RolePermission rp WHERE rp.roleId IN :roleIds")
    List<RolePermission> findAllByRoleIdIn(@Param("roleIds") List<String> roleIds);
}
