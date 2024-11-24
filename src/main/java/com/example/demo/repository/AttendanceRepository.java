package com.example.demo.repository;

import com.example.demo.model.Attendance;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByUser_IdAndDate(Long userId, Date date);
    List<Attendance> findByUser_Id(Long userId);
    @Query(value = "SELECT * FROM attendance a WHERE a.user_id = :userId AND a.date BETWEEN :startDate AND :endDate", nativeQuery = true)
    List<Attendance> findByUserIdAndDateBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    List<Attendance> findAllByUserId(Long userId);

    boolean existsByUser_IdAndDate(Long userId, LocalDate date);

    Attendance findFirstByUser_IdAndDate(Long userId, LocalDate date);




}
