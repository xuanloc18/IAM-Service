package dev.cxl.iam_service.respository.custom;

import dev.cxl.iam_service.dto.request.UserSearchRequest;
import dev.cxl.iam_service.dto.response.UserResponse;
import dev.cxl.iam_service.entity.User;

import java.util.List;

public interface UserRepositoryCustom {
    List<User> search(UserSearchRequest request);

    Long count(UserSearchRequest request);
}
