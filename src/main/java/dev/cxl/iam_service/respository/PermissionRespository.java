package dev.cxl.iam_service.respository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.cxl.iam_service.entity.Permission;

import java.util.Optional;

@Repository
public interface PermissionRespository extends JpaRepository<Permission, String> {
    Page<Permission> findAll(Pageable pageable);

    Boolean existsByResourceCodeAndScope(String resourceCode, String scope);

    Optional<Permission> findByResourceCodeAndScope(String resourceCode, String scope);

}
