package com.example.demo.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeFaceDTO {
    private Long id;
    private EmployeeInfoDTO employeeInfo;

    private String face;

    private String feature;

    private LocalDate createAt;
}
