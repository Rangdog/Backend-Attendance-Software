package com.example.demo.controller;

import com.example.demo.DTO.MonthlySalaryDTO;
import com.example.demo.model.MonthlySalary;
import com.example.demo.model.MonthlySalaryId;
import com.example.demo.service.MonthlySalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/monthly-salary")
public class MonthlySalaryController {
    @Autowired
    MonthlySalaryService monthlySalaryService;
    @GetMapping("/{employeeId}/{month}/{year}")
    public ResponseEntity<MonthlySalaryDTO> getMonthSalary(  @PathVariable Long employeeId,
                                                          @PathVariable Integer month,
                                                          @PathVariable Integer year){
        MonthlySalaryId monthlySalaryId = new MonthlySalaryId();
        monthlySalaryId.setEmployeeId(employeeId);
        monthlySalaryId.setYear(year);
        monthlySalaryId.setMonth(month);
        MonthlySalary monthlySalary = monthlySalaryService.getMonthlySalary(monthlySalaryId);
        if(monthlySalary != null){
            MonthlySalaryDTO monthlySalaryDTO = new MonthlySalaryDTO();
            monthlySalaryDTO.setNetSalary(monthlySalary.getNetSalary());
            monthlySalaryDTO.setId(monthlySalary.getId());
            monthlySalaryDTO.setCreatedAt(monthlySalary.getCreatedAt());
            monthlySalaryDTO.setTotalPenalty(monthlySalary.getTotalPenalty());
            monthlySalaryDTO.setTotalWorkDays(monthlySalary.getTotalWorkDays());
            monthlySalaryDTO.setEmployeeId(monthlySalary.getId().getEmployeeId());
            return ResponseEntity.ok(monthlySalaryDTO);
        }
        return ResponseEntity.badRequest().build();
    }


}
