package com.example.demo.repository;

import com.example.demo.model.EmployeeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeInfoRepository extends JpaRepository<EmployeeInfo,Long> {
    Boolean existsByUser_Id(Long user_Id);
    EmployeeInfo findFirstByUser_Id(Long user_Id);
}
