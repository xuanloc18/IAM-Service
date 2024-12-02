package dev.cxl.iam_service.respository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.cxl.iam_service.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Boolean existsByCode(String code);

    Page<Role> findAll(Pageable pageable);

    Optional<Role> findByCode(String code);

    @Query("SELECT r.code FROM Role r " + "JOIN UserRole ur ON r.id = ur.roleID "
            + "JOIN User u ON u.userID = ur.userID "
            + "WHERE u.userID = :userID "
            + "AND r.code = :code")
    Optional<String> findRoleName(@Param("userID") String userID, @Param("code") String code);
}
