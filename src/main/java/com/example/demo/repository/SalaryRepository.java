package com.example.demo.repository;

import com.example.demo.model.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SalaryRepository  extends JpaRepository<Salary, Long> {
    Salary findFirstByUserIdAndDateContractLessThanEqualOrderByDateContractDesc(Long userId, LocalDate date);
    Optional<Salary> findTopByUserIdOrderByDateContractDesc(Long userId);

}
