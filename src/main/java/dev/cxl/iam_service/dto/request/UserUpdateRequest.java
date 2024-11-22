package dev.cxl.iam_service.dto.request;

import java.time.LocalDate;
import java.util.Set;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {

    String passWord;
    String firstName;
    String lastName;
    LocalDate dateOfBirth;
    Set<String> roles;
}
