package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "employee_info")
public class EmployeeInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")  // Tên cột trong bảng
    private Long employeeId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable=false)
    private User user;  // Liên kết với entity User

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "birth_year")
    private String birthYear;  // Kiểu TEXT cho năm sinh

    @Column(name = "position", nullable = false)
    private Integer position; // 0 là nhân viên, 1 là trưởng phòng, 2 là quản lý
}
