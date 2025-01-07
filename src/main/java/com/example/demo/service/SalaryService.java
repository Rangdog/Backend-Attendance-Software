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

    public Salary findSalaryForMonth(Long employeeId, LocalDate month) {
        return salaryRepository.findLatestSalaryByEmployeeIdAndDate(employeeId, month);
    }

    public Optional<Salary> findLatestSalaryByUserId(Long employeeId) {
        return salaryRepository.findTopByEmployeeInfo_EmployeeIdOrderByDateContractDesc(employeeId);
    }

}
