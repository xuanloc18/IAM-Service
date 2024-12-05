package dev.cxl.iam_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSearchRequest extends SearchRequest{
    private String userName;  // Tìm kiếm theo username
    private String userMail;     // Tìm kiếm theo email
    private String firstName;
    private String lastName; // Tìm kiếm theo fullName
}
