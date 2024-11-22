package dev.cxl.iam_service.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.cxl.iam_service.entity.InvalidateToken;

@Repository
public interface InvalidateTokenRepository extends JpaRepository<InvalidateToken, String> {}
