package dev.cxl.iam_service.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.cxl.iam_service.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {}
