package com.example.demo.controller;

import com.example.demo.DTO.EmployeeInfoDTO;
import com.example.demo.model.EmployeeInfo;
import com.example.demo.model.User;
import com.example.demo.service.EmployeeInfoService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeInfoController {
    @Autowired
    private EmployeeInfoService service;

    @Autowired
    private UserService userService;

    @GetMapping()
    public ResponseEntity<List<EmployeeInfoDTO>> getAllEmployeeUnlock() {
        List<EmployeeInfoDTO> employeeInfoDTOS = new ArrayList<>();
        List<EmployeeInfo> employeeInfos = service.getAllEmployee();
        for (EmployeeInfo employeeInfo : employeeInfos){
            if(employeeInfo.getUser().isLocked()){
                continue;
            }
            EmployeeInfoDTO employeeInfoDTO = new EmployeeInfoDTO();
            employeeInfoDTO.setId(employeeInfo.getEmployeeId());
            employeeInfoDTO.setUserId(employeeInfo.getUser().getId());
            employeeInfoDTO.setFullName(employeeInfo.getFullName());
            employeeInfoDTO.setLocked(employeeInfo.getUser().isLocked());
            employeeInfoDTOS.add(employeeInfoDTO);
        }
        return ResponseEntity.ok(employeeInfoDTOS);
    }

    @GetMapping("/all")
    public ResponseEntity<List<EmployeeInfoDTO>> getAllEmployee() {
        List<EmployeeInfoDTO> employeeInfoDTOS = new ArrayList<>();
        List<EmployeeInfo> employeeInfos = service.getAllEmployee();
        for (EmployeeInfo employeeInfo : employeeInfos){
            EmployeeInfoDTO employeeInfoDTO = new EmployeeInfoDTO();
            employeeInfoDTO.setId(employeeInfo.getEmployeeId());
            employeeInfoDTO.setUserId(employeeInfo.getUser().getId());
            employeeInfoDTO.setFullName(employeeInfo.getFullName());
            employeeInfoDTO.setLocked(employeeInfo.getUser().isLocked());
            employeeInfoDTOS.add(employeeInfoDTO);
        }
        return ResponseEntity.ok(employeeInfoDTOS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeInfoDTO> getEmployee(@PathVariable Long id) {
        EmployeeInfoDTO employeeInfoDTO = new EmployeeInfoDTO();
        EmployeeInfo employeeInfo = service.getEmployeeById(id);
        if(employeeInfo == null){
            return ResponseEntity.notFound().build();
        }
        employeeInfoDTO.setId(employeeInfo.getEmployeeId());
        employeeInfoDTO.setPosition(employeeInfo.getPosition());
        employeeInfoDTO.setBirthYear(employeeInfo.getBirthYear());
        employeeInfoDTO.setUserId(employeeInfo.getUser().getId());
        employeeInfoDTO.setFullName(employeeInfo.getFullName());
        return ResponseEntity.ok(employeeInfoDTO);
    }

    @PostMapping
    public ResponseEntity<EmployeeInfo> createEmployee(@RequestBody EmployeeInfoDTO employeeInfoDTO) {
        EmployeeInfo employeeInfo = new EmployeeInfo();
        User user = userService.findById(employeeInfoDTO.getUserId());
        employeeInfo.setUser(user);
        employeeInfo.setPosition(employeeInfoDTO.getPosition());
        employeeInfo.setBirthYear(employeeInfoDTO.getBirthYear());
        employeeInfo.setFullName(employeeInfoDTO.getFullName());
        EmployeeInfo savedEmployee = service.saveEmployee(employeeInfo);
        return ResponseEntity.ok(savedEmployee);
    }

    @PutMapping
    public ResponseEntity<EmployeeInfo> updateEmployee(@RequestBody EmployeeInfoDTO employeeInfoDTO){
        System.out.println(employeeInfoDTO.toString());
        EmployeeInfo employeeInfoUpdate = service.getEmployeeById(employeeInfoDTO.getId());
        employeeInfoUpdate.setFullName(employeeInfoDTO.getFullName());
        employeeInfoUpdate.setPosition(employeeInfoDTO.getPosition());
        employeeInfoUpdate.setBirthYear(employeeInfoDTO.getBirthYear());
        EmployeeInfo updatedEmploy = service.saveEmployee(employeeInfoUpdate);
        return ResponseEntity.ok(updatedEmploy);
    }

    @GetMapping("/check/{userId}")
    public ResponseEntity<?> checkEmployeeInfo(@PathVariable Long userId) {
        boolean exists = service.checkExist(userId);
        if (exists) {
            EmployeeInfo employeeInfo = service.findByUserId(userId);
            return ResponseEntity.ok(employeeInfo);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee info not found");
        }
    }
}
