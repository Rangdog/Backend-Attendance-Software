package com.example.demo.service;

import com.example.demo.model.EmployeeInfo;
import com.example.demo.repository.EmployeeInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeInfoService {
    @Autowired
    private EmployeeInfoRepository repository;

    public EmployeeInfo getEmployeeById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public EmployeeInfo saveEmployee(EmployeeInfo employeeInfo) {
        return repository.save(employeeInfo);
    }

    public Boolean checkExist(Long userId){
        return repository.existsByUser_Id(userId);
    }

    public EmployeeInfo findByUserId(Long userId){
        return repository.findFirstByUser_Id(userId);
    }

    public List<EmployeeInfo> getAllEmployee(){
        return repository.findAll();
    }
}
