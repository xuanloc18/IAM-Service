package dev.cxl.iam_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "invalid_refresh_tokens")
public class InvalidateRefreshToken extends AuditableEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    String id;
}
