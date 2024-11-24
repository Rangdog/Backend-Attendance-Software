package com.example.demo.controller;

import com.example.demo.model.AttendanceHistory;
import com.example.demo.service.AttendanceHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
public class AttendanceHistoryController {
    @Autowired
    private AttendanceHistoryService attendanceHistoryService;

    // Endpoint để lấy lịch sử chấm công theo userId
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<AttendanceHistory>> getHistoryByUserId(@PathVariable Long userId) {
//        List<AttendanceHistory> historyList = attendanceHistoryService.getHistoryByUserId(userId);
//        return ResponseEntity.ok(historyList);
//    }
}
