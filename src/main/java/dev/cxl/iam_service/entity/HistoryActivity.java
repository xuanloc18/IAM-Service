package dev.cxl.iam_service.entity;

import java.time.LocalDateTime;

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
public class HistoryActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String activityId;

    String userID;
    String activityType;
    String activityName;
    LocalDateTime activityStart;
    String browserID;
}
