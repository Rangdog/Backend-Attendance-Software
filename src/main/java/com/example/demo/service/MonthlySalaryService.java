package com.example.demo.service;

import com.example.demo.model.MonthlySalary;
import com.example.demo.model.MonthlySalaryId;
import com.example.demo.repository.MonthlySalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MonthlySalaryService {
    @Autowired
    private MonthlySalaryRepository repository;

    public List<MonthlySalary> getAll() {
        return repository.findAll();
    }

    public MonthlySalary save(MonthlySalary salary) {
        return repository.save(salary);
    }
    public boolean checkIfMonthlySalaryExists(MonthlySalaryId monthlySalaryId) {
        return repository.existsById(monthlySalaryId);
    }

    public MonthlySalary getMonthlySalary(MonthlySalaryId monthlySalaryId){
        return repository.findById(monthlySalaryId).orElse(null);
    }
}
