package com.example.demo.repository;

import com.example.demo.model.RequestLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestLeaveRepository extends JpaRepository<RequestLeave, Long> {
    List<RequestLeave> findByUserId(Long userId);

    @Query(value = "SELECT r FROM RequestLeave r WHERE r.approve = true AND r.user.id = :userId")
    List<RequestLeave> getApprovedLeavesByUserId(Long userId);


}
