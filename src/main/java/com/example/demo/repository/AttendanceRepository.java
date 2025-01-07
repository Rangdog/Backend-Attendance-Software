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
    @Query(value = "SELECT * FROM attendance a WHERE a.employee_id = :employeeId AND a.date BETWEEN :startDate AND :endDate", nativeQuery = true)
    List<Attendance> findByEmployeeIdAndDateBetween(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    boolean existsByEmployeeInfo_EmployeeIdAndDate(Long employee_id, LocalDate date);
    Attendance findFirstByEmployeeInfo_EmployeeIdAndDate(Long employee_id, LocalDate date);
}
