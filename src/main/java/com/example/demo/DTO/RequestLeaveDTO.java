package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestLeaveDTO {
    private Long id;
    private String startTime;
    private String endTime;
    private String reason;
    private Long userId;
    private String userName;
    private Boolean approve;
}
