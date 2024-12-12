package dev.cxl.iam_service.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.cxl.iam_service.entity.UserInformation;

@Repository
public interface UserInformationRepository extends JpaRepository<UserInformation, String> {
    boolean existsUserInformationByUsername(String userName);
}
