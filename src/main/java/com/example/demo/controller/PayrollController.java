package com.example.demo.controller;

import com.example.demo.model.Payroll;
import com.example.demo.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payroll")
public class PayrollController {
    @Autowired
    private PayrollService payrollService;

    // Endpoint để tính lương cho nhân viên
    @PostMapping("/calculate/{userId}/{month}/{year}")
    public ResponseEntity<Payroll> calculatePayroll(@PathVariable Long userId,
                                                    @PathVariable int month,
                                                    @PathVariable int year) {
        Payroll payroll = payrollService.calculatePayroll(userId, month, year);
        return ResponseEntity.ok(payroll);
    }

    // Endpoint để xem lương của nhân viên
    @GetMapping("/user/{userId}/month/{month}/year/{year}")
    public ResponseEntity<Payroll> getPayroll(@PathVariable Long userId,
                                              @PathVariable int month,
                                              @PathVariable int year) {
        // Logic để lấy thông tin lương
        Payroll payroll = payrollService.getPayrollByUserIdAndMonthYear(userId, month, year);
        return ResponseEntity.ok(payroll);
    }
}
