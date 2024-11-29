package dev.cxl.iam_service.dto.response;

import java.time.LocalDate;
import java.util.Set;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

    String userID;
    String userKCLID;
    String userName;
    String userMail;
    String firstName;
    String lastName;
    LocalDate dateOfBirth;
    Boolean enabled;
    Boolean deleted;
    String avatar;
    Set<String> roles;
}
