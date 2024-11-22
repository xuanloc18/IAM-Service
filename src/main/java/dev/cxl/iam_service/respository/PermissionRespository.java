package dev.cxl.iam_service.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.cxl.iam_service.entity.Permission;

@Repository
public interface PermissionRespository extends JpaRepository<Permission, String> {}
