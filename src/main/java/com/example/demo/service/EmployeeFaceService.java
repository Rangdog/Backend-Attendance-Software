package com.example.demo.service;

import com.example.demo.model.EmployeeFace;
import com.example.demo.repository.EmployeeFaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeFaceService {
    @Autowired
    private EmployeeFaceRepository repository;

    public List<EmployeeFace> getAllFaces() {
        return repository.findAll();
    }

    public EmployeeFace getFaceById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public EmployeeFace saveFace(EmployeeFace employeeFace) {
        return repository.save(employeeFace);
    }

    public void deleteFace(Long id) {
        repository.deleteById(id);
    }

    public List<EmployeeFace> getAllByEmployeeId(Long employeeId){
        return repository.findAllByEmployeeInfo_EmployeeId(employeeId);
    }
}
