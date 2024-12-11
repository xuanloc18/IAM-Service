package dev.cxl.iam_service.respository.custom;

import java.util.List;

import dev.cxl.iam_service.dto.request.UserSearchRequest;
import dev.cxl.iam_service.entity.User;

public interface UserRepositoryCustom {
    List<User> search(UserSearchRequest request);

    Long count(UserSearchRequest request);
}
