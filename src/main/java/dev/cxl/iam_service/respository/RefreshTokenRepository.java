package dev.cxl.iam_service.respository;

import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.cxl.iam_service.entity.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    @Transactional
    void deleteByAccessTokenID(String string);

    Optional<RefreshToken> findRefreshTokenByRefreshToken(String string);
}
