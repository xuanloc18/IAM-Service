package dev.cxl.iam_service.respository;
import dev.cxl.iam_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRespository extends JpaRepository<User,String> {
    Optional<User> findByUserMail(String userMail);
    boolean existsByUserMail(String userMail);
}
