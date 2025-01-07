package com.example.demo.repository;

import com.example.demo.model.RequestLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RequestLeaveRepository extends JpaRepository<RequestLeave, Long> {
    List<RequestLeave> findAllByEmployeeInfo_EmployeeId(Long employeeId);
    List<RequestLeave> findAllByOrderByStartTimeDesc();
    @Query(value = "SELECT r FROM RequestLeave r WHERE (r.approve = 1 OR r.approve = 2) AND r.employeeInfo.employeeId = :employeeId")
    List<RequestLeave> getApprovedLeavesByEmployeeInfo_EmployeeId(Long employeeId);

    @Query(value = "SELECT r FROM RequestLeave r WHERE r.approve = 0")
    List<RequestLeave> getUnApprovedLeaves();

    @Query("SELECT rl FROM RequestLeave rl " +
            "WHERE FUNCTION('DATE', rl.startTime) = FUNCTION('DATE', :date) " +
            "AND FUNCTION('DATE', rl.endTime) = FUNCTION('DATE', :date) " +
            "AND rl.employeeInfo.employeeId = :employeeId")
    List<RequestLeave> findByDateAndEmployeeId(@Param("date") LocalDateTime date, @Param("employeeId") Long employeeId);

}
