package dev.cxl.iam_service.respository;

import dev.cxl.iam_service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import dev.cxl.iam_service.entity.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Boolean existsByCode(String code);
    Page<Role> findAll(Pageable pageable);
    Optional<Role> findByCode(String code);
}
