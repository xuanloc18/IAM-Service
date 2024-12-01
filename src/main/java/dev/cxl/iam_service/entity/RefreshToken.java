package dev.cxl.iam_service.entity;

import java.util.Date;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "refresh_tokens") // Đặt tên bảng rõ ràng
public class RefreshToken extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    String id;

    @Column(name = "refresh_token", nullable = false, unique = true, length = 1500)
    String refreshToken;

    @Column(name = "access_token_id", nullable = false, length = 500)
    String accessTokenID;

    @Column(name = "expires_access_token", nullable = false)
    Date expiresAccessToken;
}
