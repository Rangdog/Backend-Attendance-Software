package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeInfoDTO {
    private Long id;
    private Long userId;
    private String fullName;
    private String birthYear;
    private int position;
    private boolean isLocked;
}
