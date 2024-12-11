package dev.cxl.iam_service.dto.response;

import dev.cxl.iam_service.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Files extends AuditableEntity {
    String iD;
    String fileName;
    String fileType;
    Long fileSize;
    String filePath;
    String fileVersion;
    Boolean visibility;
    String ownerId;
    Boolean deleted;
}
