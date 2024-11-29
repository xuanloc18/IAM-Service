package dev.cxl.iam_service.entity;

import java.util.Set;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role extends AuditableEntity {

    @Id
    String name;

    String description;
    Set<String> permissions;
}
