package com.example.demo.service;

import com.example.demo.model.Action;
import com.example.demo.model.AttendanceHistory;
import com.example.demo.model.User;
import com.example.demo.repository.AttendanceHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceHistoryService {
    @Autowired
    private AttendanceHistoryRepository attendanceHistoryRepository;

    public AttendanceHistory save(AttendanceHistory history) {
        return attendanceHistoryRepository.save(history);
    }
    public void logAction(Long userId, Action action) {
        AttendanceHistory history = new AttendanceHistory();
        history.setUser(new User(userId));
        history.setAction(action);
        attendanceHistoryRepository.save(history);
    }
    public List<AttendanceHistory> getHistoryByUserId(Long userId) {
        return attendanceHistoryRepository.findByUserId(userId);
    }
}
