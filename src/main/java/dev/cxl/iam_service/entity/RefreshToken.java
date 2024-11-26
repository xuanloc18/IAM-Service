package dev.cxl.iam_service.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String refreshToken;
    String accessTokenID;
    Date expiresAccessToken;
}
