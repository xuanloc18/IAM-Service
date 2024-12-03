package dev.cxl.iam_service.respository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.cxl.iam_service.entity.Permission;

@Repository
public interface PermissionRespository extends JpaRepository<Permission, String> {
    Page<Permission> findAll(Pageable pageable);

    Boolean existsByResourceCodeAndScope(String resourceCode, String scope);

    Optional<Permission> findByResourceCodeAndScope(String resourceCode, String scope);

    @Query("SELECT p.id FROM Permission p " + "JOIN RolePermission rp ON p.id = rp.permissionId "
            + "JOIN UserRole ur ON rp.roleId = ur.roleID "
            + "JOIN User u ON ur.userID = u.userID "
            + "WHERE u.userID = :userID "
            + "AND p.resourceCode = :resourceCode "
            + "AND p.scope = :scope ")
    Optional<String> findPermissionIdByUserAndScope(
            @Param("userID") String userID, @Param("resourceCode") String resourceCode, @Param("scope") String scope);

    @Query("SELECT p.id FROM Permission p " + "JOIN RolePermission rp ON p.id = rp.permissionId "
            + "JOIN UserRole ur ON rp.roleId = ur.roleID "
            + "JOIN User u ON ur.userID = u.userID "
            + "WHERE u.userKCLID = :userKCLID "
            + "AND p.resourceCode = :resourceCode "
            + "AND p.scope = :scope ")
    Optional<String> findPermissionIdByUserKCLAndScope(
            @Param("userKCLID") String userKCLID,
            @Param("resourceCode") String resourceCode,
            @Param("scope") String scope);
}
