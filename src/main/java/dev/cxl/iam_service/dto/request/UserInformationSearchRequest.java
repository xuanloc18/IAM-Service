package dev.cxl.iam_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInformationSearchRequest extends SearchRequest {
    private String username;

    private String fullName;
    private LocalDate dateOfBirth;
    private String streetName;
    private String ward;
    private String district;
    private String province;
    private Integer yearsOfExperience;
    private Boolean deleted;
}
