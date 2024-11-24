package com.example.demo.service;

import com.example.demo.model.Salary;
import com.example.demo.repository.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class SalaryService {
    @Autowired
    private SalaryRepository salaryRepository;

    public Salary createSalary(Salary salary){
        return salaryRepository.save(salary);
    }

    public Salary findSalaryForMonth(Long userId, LocalDate month) {
        return salaryRepository.findFirstByUserIdAndDateContractLessThanEqualOrderByDateContractDesc(userId, month);
    }

    public Optional<Salary> findLatestSalaryByUserId(Long userId) {
        return salaryRepository.findTopByUserIdOrderByDateContractDesc(userId);
    }

}
