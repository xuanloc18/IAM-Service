package dev.cxl.iam_service.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {

    String userMail;

    @Size(min = 8, message = "PASSWORD_EXCEPION")
    String passWord;

    String firstName;
    String lastName;
    LocalDate dateOfBirth;
}
