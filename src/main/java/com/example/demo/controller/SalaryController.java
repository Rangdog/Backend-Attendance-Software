package com.example.demo.controller;

import com.example.demo.DTO.SalaryDTO;
import com.example.demo.model.Salary;
import com.example.demo.model.User;
import com.example.demo.service.SalaryService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/salaries")
public class SalaryController {
    @Autowired
    private SalaryService salaryService;
    @Autowired
    private UserService userService;

    @GetMapping("/{userId}/{month}")
    public Salary getSalaryForMonth(
            @PathVariable Long userId,
            @PathVariable String month
    ) {
        LocalDate date = LocalDate.parse(month + "-01"); // Format: yyyy-MM
        return salaryService.findSalaryForMonth(userId, date);
    }

    @PostMapping
    public Salary createSalary(@RequestBody SalaryDTO salaryDTO) {
        Salary salary = new Salary();
        User user = userService.findById(salaryDTO.getUserId());
        salary.setSalary(salaryDTO.getSalary());
        salary.setUser(user);
        salary.setDateContract(salaryDTO.getDateContract());
        return salaryService.createSalary(salary);
    }

    @GetMapping("/current/{userId}")
    public ResponseEntity<Salary> getCurrentSalary(@PathVariable Long userId) {
        Optional<Salary> salary = salaryService.findLatestSalaryByUserId(userId);
        return salary.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
