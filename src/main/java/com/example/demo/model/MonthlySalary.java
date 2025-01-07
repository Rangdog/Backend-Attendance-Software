package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_salary")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySalary {

    @EmbeddedId
    private MonthlySalaryId id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "total_work_days")
    private Double totalWorkDays;

    @Column(name = "total_penalty")
    private Double totalPenalty;

    @Column(name = "net_salary")
    private Double netSalary;

    @ManyToOne
    @MapsId("employeeId") // Maps khóa "employeeId" từ EmbeddedId
    @JoinColumn(name = "employee_id") // Liên kết với cột trong DB
    private EmployeeInfo employeeInfo;
}
