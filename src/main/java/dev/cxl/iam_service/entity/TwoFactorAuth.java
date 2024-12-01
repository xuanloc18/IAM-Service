package dev.cxl.iam_service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "two_factor_auth")
public class TwoFactorAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    String id;

    @Column(name = "user_id")
    String userId;

    @Column(name = "user_email")
    String userMail;

    @Column(name = "otp")
    String otp;

    @Column(name = "created_at")
    LocalDateTime created;

    @Column(name = "expires_at")
    LocalDateTime expires;
}
