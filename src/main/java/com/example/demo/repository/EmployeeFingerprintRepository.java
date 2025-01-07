package com.example.demo.repository;

import com.example.demo.model.EmployeeFingerprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeFingerprintRepository extends JpaRepository<EmployeeFingerprint,Long> {

}
