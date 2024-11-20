package dev.cxl.iam_service.mapper;

import dev.cxl.iam_service.dto.request.UserCreationRequest;
import dev.cxl.iam_service.dto.request.UserUpdateRequest;
import dev.cxl.iam_service.dto.response.UserResponse;
import dev.cxl.iam_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
//   @Mapping(target = "lastName",ignore = true)
    UserResponse toUserResponse(User user );
    @Mapping(target = "roles",ignore = true)
    User updateUser(@MappingTarget User user, UserUpdateRequest request);
}
