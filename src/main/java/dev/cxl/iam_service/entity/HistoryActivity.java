package dev.cxl.iam_service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "history_activities") // Đặt tên bảng
public class HistoryActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    String id;

    @Column(name = "user_id", nullable = false)
    String userID;

    @Column(name = "activity_type", nullable = false)
    String activityType;

    @Column(name = "activity_name", nullable = false)
    String activityName;

    @Column(name = "activity_start", nullable = false)
    LocalDateTime activityStart;

    @Column(name = "browser_id")
    String browserID;
}
