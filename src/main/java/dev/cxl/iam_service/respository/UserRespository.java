package dev.cxl.iam_service.respository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.cxl.iam_service.entity.User;

@Repository
public interface UserRespository extends JpaRepository<User, String> {
    Optional<User> findByUserMail(String userMail);
    Optional<User> findByUserKCLID(String string);
    boolean deleteByUserMail(String userMail);
    boolean existsByUserMail(String userMail);
    boolean existsByUserName(String userName);
    Page<User> findAll(Pageable pageable);
}
