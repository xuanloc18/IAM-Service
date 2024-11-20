package dev.cxl.iam_service.respository;
import dev.cxl.iam_service.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRespository extends JpaRepository<Permission,String> {
}
