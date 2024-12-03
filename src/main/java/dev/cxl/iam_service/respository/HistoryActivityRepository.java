package dev.cxl.iam_service.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.cxl.iam_service.entity.HistoryActivity;

public interface HistoryActivityRepository extends JpaRepository<HistoryActivity, String> {}
