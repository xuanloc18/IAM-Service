package dev.cxl.iam_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.cxl.iam_service.entity.HistoryActivity;
import dev.cxl.iam_service.respository.HistoryActivityRepository;

@Service
public class ActivityService {
    @Autowired
    HistoryActivityRepository historyActivityRepository;

    public void createHistoryActivity(HistoryActivity historyActivity) {
        historyActivityRepository.save(historyActivity);
    }
}
