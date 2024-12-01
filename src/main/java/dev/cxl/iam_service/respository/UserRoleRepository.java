package dev.cxl.iam_service.respository;

import dev.cxl.iam_service.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole,String> {
    List<UserRole> findByUserID(String userId);
}
