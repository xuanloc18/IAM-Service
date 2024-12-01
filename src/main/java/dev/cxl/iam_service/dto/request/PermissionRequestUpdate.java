package dev.cxl.iam_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionRequestUpdate {

    Boolean deleted;
    String name;
    String resourceCode;
    String scope;
}
