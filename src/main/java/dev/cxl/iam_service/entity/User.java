package dev.cxl.iam_service.entity;

import java.time.LocalDate;
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
@Table(name = "users")
public class User extends AuditableEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String userID;

    String userKCLID;
    String userName;
    String userMail;
    String passWord;
    String firstName;
    String lastName;
    LocalDate dateOfBirth;
    String avatar;
    Boolean enabled;
    Boolean deleted;
    Set<String> roles;
}
