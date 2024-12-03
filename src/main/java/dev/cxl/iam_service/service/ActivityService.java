package dev.cxl.iam_service.service;

import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.cxl.iam_service.entity.HistoryActivity;
import dev.cxl.iam_service.enums.UserAction;
import dev.cxl.iam_service.respository.HistoryActivityRepository;

@Service
public class ActivityService {
    @Autowired
    HistoryActivityRepository historyActivityRepository;

    @Autowired
    HttpServletRequest request;

    public void createHistoryActivity(String userId, UserAction action) {

        historyActivityRepository.save(HistoryActivity.builder()
                .activityType(action.name())
                .activityName(action.getDescription())
                .userID(userId)
                .activityStart(LocalDateTime.now())
                .browserID(request.getRemoteAddr())
                .build());
    }
}
