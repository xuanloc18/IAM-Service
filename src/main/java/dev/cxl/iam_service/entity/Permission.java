package dev.cxl.iam_service.entity;

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
public class Permission extends  AuditableEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    Boolean deleted;
    String name;
    String resource_code;
    String scope;
}
