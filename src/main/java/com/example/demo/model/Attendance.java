package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="attendance")
@Getter
@Setter
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private EmployeeInfo employeeInfo;

    private LocalDateTime checkIn;

    private LocalDateTime checkOut;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "face_in", columnDefinition = "MEDIUMBLOB")
    @Lob
    private byte[] faceIn;

    @Column(name = "face_out", columnDefinition = "MEDIUMBLOB")
    @Lob
    private byte[] faceOut;

    public boolean isFinish(){
        return checkIn != null && checkOut != null ;
    }
}
