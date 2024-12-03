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
@Table(name = "roles") // Đặt tên bảng rõ ràng
public class Role extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    String id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    String name;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    String code;

    @Column(name = "description", length = 255)
    String description;

    @Column(name = "deleted", nullable = false)
    Boolean deleted = false;
}
