package com.example.demo.controller;

import com.example.demo.DTO.EmployeeFaceDTO;
import com.example.demo.DTO.EmployeeInfoDTO;
import com.example.demo.model.EmployeeFace;
import com.example.demo.service.EmployeeFaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/employee-faces")
public class EmployeeFaceController {
    @Autowired
    private EmployeeFaceService service;
    @GetMapping
    public List<EmployeeFaceDTO> getAllFaces() {
        List<EmployeeFaceDTO> employeeFaceDTOS = new ArrayList<>();
        List<EmployeeFace> employeeFaces = service.getAllFaces();
        for (EmployeeFace employeeFace : employeeFaces){
            EmployeeFaceDTO employeeFaceDTO =  new EmployeeFaceDTO();
            EmployeeInfoDTO employeeInfoDTO = new EmployeeInfoDTO();
            employeeFaceDTO.setId(employeeFace.getId());
            employeeFaceDTO.setFace(employeeFace.getFace());
            employeeFaceDTO.setFeature(employeeFace.getFeature());
            employeeFaceDTO.setCreateAt(employeeFace.getCreateAt());
            //infor
            employeeInfoDTO.setFullName(employeeFace.getEmployeeInfo().getFullName());
            employeeInfoDTO.setId(employeeFace.getEmployeeInfo().getEmployeeId());
            employeeFaceDTO.setEmployeeInfo(employeeInfoDTO);
            employeeFaceDTOS.add(employeeFaceDTO);
        }
        return employeeFaceDTOS;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeFace> getFaceById(@PathVariable Long id) {
        EmployeeFace employeeFace = service.getFaceById(id);
        return ResponseEntity.ok(employeeFace);
    }

    @PostMapping
    public EmployeeFace createFace(@RequestBody EmployeeFace employeeFace) {
        return service.saveFace(employeeFace);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFace(@PathVariable Long id) {
        service.deleteFace(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/all/{employeeId}")
    public ResponseEntity<List<EmployeeFaceDTO>> getAllEmployeeFaceByEmployeeId(@PathVariable Long employeeId){
        List<EmployeeFace> employeeFaces = service.getAllByEmployeeId(employeeId);
        List<EmployeeFaceDTO> employeeFaceDTOS = new ArrayList<>();
        for(EmployeeFace employeeFace: employeeFaces){
            EmployeeFaceDTO employeeFaceDTO =new EmployeeFaceDTO();
            employeeFaceDTO.setId(employeeFace.getId());
            employeeFaceDTO.setFace(employeeFace.getFace());
            employeeFaceDTOS.add(employeeFaceDTO);
        }
        return ResponseEntity.ok(employeeFaceDTOS);
    }

    @GetMapping("/check/{employeeId}")
    public ResponseEntity<Boolean> checkFaceRegister(@PathVariable Long employeeId){
        List<EmployeeFace> employeeFaces = service.getAllByEmployeeId(employeeId);
        if(employeeFaces.size() > 0){
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);
    }
}
