package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {
    private Long id;
    private Long userId;

    private String checkIn;

    private String checkOut;

    private String date;

    private byte[] faceIn;

    private byte[] faceOut;

    private boolean isFinish;

    private boolean leaveRequest;
    
}
