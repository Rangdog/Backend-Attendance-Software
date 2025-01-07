package com.example.demo.controller;


import com.example.demo.DTO.EmployeeFaceDTO;
import com.example.demo.DTO.EmployeeFingerprintDTO;
import com.example.demo.DTO.EmployeeInfoDTO;
import com.example.demo.model.EmployeeFace;
import com.example.demo.model.EmployeeFingerprint;
import com.example.demo.service.EmployeeFingerprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/employee-fingerprint")
public class EmployeeFingerprintController {
    @Autowired
    private EmployeeFingerprintService employeeFingerprintService;

    @GetMapping
    public List<EmployeeFingerprintDTO> getAllFaces() {
        List<EmployeeFingerprintDTO> employeeFingerprintDTOS = new ArrayList<>();
        List<EmployeeFingerprint> employeeFingerprints = employeeFingerprintService.getAllFingerprint();
        for (EmployeeFingerprint employeeFingerprint : employeeFingerprints){
            EmployeeFingerprintDTO employeeFaceDTO = new EmployeeFingerprintDTO();
            employeeFaceDTO.setFingerprint(employeeFingerprint.getFingerprint());
            employeeFaceDTO.setFeature(employeeFingerprint.getFeature());
            employeeFaceDTO.setId(employeeFingerprint.getId());
            employeeFaceDTO.setEmployeeId(employeeFingerprint.getEmployeeInfo().getEmployeeId());
            employeeFaceDTO.setCreateAt(employeeFingerprint.getCreateAt());
            employeeFaceDTO.setFullName(employeeFingerprint.getEmployeeInfo().getFullName());
            employeeFingerprintDTOS.add(employeeFaceDTO);
        }
        return employeeFingerprintDTOS;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFace(@PathVariable Long id) {
        employeeFingerprintService.deleteEmployeeFingerprint(id);
        return ResponseEntity.noContent().build();
    }

}
