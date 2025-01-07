package com.example.demo.DTO;

import com.example.demo.model.EmployeeInfo;
import com.example.demo.model.MonthlySalaryId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySalaryDTO {
    private MonthlySalaryId id;
    private LocalDateTime createdAt;
    private Double totalWorkDays;
    private Double totalPenalty;
    private Double netSalary;
    private Long employeeId;
}
