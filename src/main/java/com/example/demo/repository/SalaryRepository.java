package com.example.demo.repository;

import com.example.demo.model.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SalaryRepository  extends JpaRepository<Salary, Long> {
    Salary findFirstByEmployeeInfo_EmployeeIdAndDateContractLessThanEqualOrderByDateContractDesc(Long employeeId, LocalDate date);
    Optional<Salary> findTopByEmployeeInfo_EmployeeIdOrderByDateContractDesc(Long employeeId);

    @Query(value = "SELECT * FROM salaries s WHERE s.employee_id = :employeeId AND s.date <= :date ORDER BY s.date DESC, s.id DESC LIMIT 1", nativeQuery = true)
    Salary findLatestSalaryByEmployeeIdAndDate(@Param("employeeId") Long employeeId, @Param("date") LocalDate date);
}
