package com.example.demo.repository;

import com.example.demo.model.AttendanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceHistoryRepository extends JpaRepository<AttendanceHistory, Long> {
    List<AttendanceHistory> findByUserId(Long userId);

}
