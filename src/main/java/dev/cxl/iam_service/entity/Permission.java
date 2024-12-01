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
@Table(name = "permissions")
public class Permission extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    String id;

    @Column(name = "deleted", nullable = false)
    Boolean deleted = false;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    String name;

    @Column(name = "resource_code", nullable = false, length = 50)
    String resourceCode;

    @Column(name = "scope", nullable = false, length = 50)
    String scope;
}
