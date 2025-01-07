package com.example.demo.service;

import com.example.demo.model.EmployeeFingerprint;
import com.example.demo.repository.EmployeeFingerprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeFingerprintService {
    @Autowired
    private EmployeeFingerprintRepository employeeFingerprintRepository;

    public List<EmployeeFingerprint> getAllFingerprint(){
        return employeeFingerprintRepository.findAll();
    }

    public void deleteEmployeeFingerprint(Long employeeId){
         employeeFingerprintRepository.deleteById(employeeId);
    }
}
