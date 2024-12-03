package dev.cxl.iam_service.respository;

import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.cxl.iam_service.entity.TwoFactorAuth;

@Repository
public interface TwoFactorAuthRepository extends JpaRepository<TwoFactorAuth, String> {

    Optional<TwoFactorAuth> findFirstByUserMailOrderByCreatedDesc(String userId);

    @Transactional
    void deleteByUserId(String userId);
}
