package dev.cxl.iam_service.respository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.cxl.iam_service.entity.User;

@Repository
public interface UserRespository extends JpaRepository<User, String> {
    Optional<User> findByUserMail(String userMail);

    boolean deleteByUserMail(String userMail);

    boolean existsByUserMail(String userMail);
}
