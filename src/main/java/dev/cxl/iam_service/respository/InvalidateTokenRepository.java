package dev.cxl.iam_service.respository;

import dev.cxl.iam_service.entity.InvalidateToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidateTokenRepository extends JpaRepository<InvalidateToken,String> {
}
