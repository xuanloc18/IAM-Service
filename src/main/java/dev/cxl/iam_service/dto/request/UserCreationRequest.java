package dev.cxl.iam_service.dto.request;

import java.time.LocalDate;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {

    String userName;
    String userMail;
    String passWord;
    String firstName;
    String lastName;
    LocalDate dateOfBirth;
    String avatar;
    Boolean deleted = false;

}
