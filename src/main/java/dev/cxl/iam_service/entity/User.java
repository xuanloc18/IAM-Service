package dev.cxl.iam_service.entity;

import java.time.LocalDate;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "user_accounts") // Tên bảng đặt theo chuẩn số nhiều
public class User extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id") // Tên cột dạng snake_case
    String userID;

    @Column(name = "user_kcl_id")
    String userKCLID;

    @Column(name = "user_profile")
    String profile;

    @Column(name = "user_name")
    String userName;

    @Column(name = "user_mail")
    String userMail;

    @Column(name = "password")
    String passWord;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    @Column(name = "date_of_birth")
    LocalDate dateOfBirth;

    @Column(name = "avatar_url")
    String avatar;

    @Column(name = "is_enabled")
    Boolean enabled;

    @Column(name = "deleted")
    Boolean deleted = false;
}
