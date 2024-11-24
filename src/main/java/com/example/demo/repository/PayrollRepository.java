package com.example.demo.repository;

import com.example.demo.model.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    List<Payroll> findByUser_Id(Long userID);
    Optional<Payroll> findByUserIdAndMonthAndYear(Long userId, int month, int year);
}
