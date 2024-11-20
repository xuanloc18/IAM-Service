package dev.cxl.iam_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

         String userID;
         String userMail;
         String firstName;
         String lastName;
         LocalDate dateOfBirth;
         Set<RoleResponse> roles;
}
