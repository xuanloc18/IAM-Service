package dev.cxl.iam_service.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilesSearchRequest extends SearchRequest {
    String fileName;
    String fileType;
    Long fileSize;
    String fileVersion;
    Boolean visibility;
    String ownerId;
    Boolean deleted;
}
