package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeFingerprintDTO {
    private Long id;
    private Long employeeId;
    private String fingerprint;
    private String fullName;
    private String feature;
    private LocalDate createAt;
}
