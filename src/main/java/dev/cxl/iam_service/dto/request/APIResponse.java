package dev.cxl.iam_service.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIResponse<T> {
    @Builder.Default // mặc định sử dụng thuộc tính đã có giá trị có sẵn mà không tạo mới giá trị
    int code = 1000;

    String mesage;
    T result;
}
