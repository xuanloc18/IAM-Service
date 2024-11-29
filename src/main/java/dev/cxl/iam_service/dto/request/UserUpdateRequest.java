package dev.cxl.iam_service.dto.request;

import java.time.LocalDate;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserUpdateRequest {

    String passWord;
    String firstName;
    String lastName;
    LocalDate dateOfBirth;
    Boolean enabled;
    Boolean deleted;
    Set<String> roles;
}
