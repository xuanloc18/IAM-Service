package dev.cxl.iam_service.dto.identity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TokenExchangeResponseUser {
    String accessToken;
    String expiresIn;
    String refreshExpiresIn;
    String refreshToken;
    String tokenType;
    String idToken;
    String notBeforePolicy;
    String sessionState;
    String scope;
}
