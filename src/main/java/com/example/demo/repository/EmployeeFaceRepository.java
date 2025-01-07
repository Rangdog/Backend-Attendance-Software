package com.example.demo.repository;

import com.example.demo.model.EmployeeFace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeFaceRepository  extends JpaRepository<EmployeeFace, Long> {
    List<EmployeeFace> findAllByEmployeeInfo_EmployeeId(Long employeeId);
}
