package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "employee_fingerprints")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeFingerprint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private EmployeeInfo employeeInfo;

    @Column(name = "fingerprint", columnDefinition = "LONGTEXT")
    @Lob
    private String fingerprint;

    @Column(name = "feature", columnDefinition = "LONGTEXT")
    @Lob
    private String feature;

    @Column(name = "create_at", columnDefinition = "DATE")
    private LocalDate createAt;
}
