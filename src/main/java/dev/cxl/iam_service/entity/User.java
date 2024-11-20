package dev.cxl.iam_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
     String userID;
    
     String userMail;
     String passWord;
     String firstName;
     String lastName;
     LocalDate dateOfBirth;
     @ManyToMany(fetch = FetchType.EAGER)
     Set<Role> roles;


}
