package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckOutResponse {
    private Object result;
    private Long attendanceId;
    private String name;
    private byte[] image;

    public CheckOutResponse(Object result, Long attendanceId, String name) {
        this.result = result;
        this.attendanceId = attendanceId;
        this.name = name;
    }
}
