package com.example.demo.repository;

import com.example.demo.model.MonthlySalary;
import com.example.demo.model.MonthlySalaryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthlySalaryRepository extends JpaRepository<MonthlySalary, MonthlySalaryId> {
}
