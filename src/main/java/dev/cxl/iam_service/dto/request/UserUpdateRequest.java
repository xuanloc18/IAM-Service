package dev.cxl.iam_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

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
   List<String> roles;


}



