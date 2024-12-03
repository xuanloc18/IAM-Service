package dev.cxl.iam_service.entity;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "invalid_tokens")
public class InvalidateToken extends AuditableEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    String id;
}
