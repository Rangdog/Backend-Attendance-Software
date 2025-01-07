package com.example.demo.controller;

import com.example.demo.DTO.RequestLeaveDTO;
import com.example.demo.model.Attendance;
import com.example.demo.model.EmployeeInfo;
import com.example.demo.model.RequestLeave;
import com.example.demo.model.User;
import com.example.demo.service.AttendanceService;
import com.example.demo.service.EmployeeInfoService;
import com.example.demo.service.RequestLeaveService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/request-leave")
public class RequestLeaveController {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Autowired
    private RequestLeaveService requestLeaveService;
    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private UserService userService;
    @Autowired
    private EmployeeInfoService employeeInfoService;

    @GetMapping
    public List<RequestLeaveDTO> getALLRequestLeave(){
        List<RequestLeaveDTO> leaveDTOS = new ArrayList<>();
        List<RequestLeave> leaves = requestLeaveService.getAllRequests();
        for (RequestLeave leave : leaves){
            if(leave.getEmployeeInfo().getUser().isLocked()){
                continue;
            }
            RequestLeaveDTO requestLeaveDTO = new RequestLeaveDTO();
            requestLeaveDTO.setUserName(leave.getEmployeeInfo().getUser().getUserName());
            requestLeaveDTO.setStartTime(leave.getStartTime().format(formatter));
            requestLeaveDTO.setEndTime(leave.getEndTime().format(formatter));
            requestLeaveDTO.setApprove(leave.getApprove());
            requestLeaveDTO.setReason(leave.getReason());
            requestLeaveDTO.setName(leave.getEmployeeInfo().getFullName());
            requestLeaveDTO.setId(leave.getId());
            requestLeaveDTO.setEmployee_id(leave.getEmployeeInfo().getEmployeeId());
            leaveDTOS.add(requestLeaveDTO);
        }
        return leaveDTOS;
    }

    @GetMapping("/employee/{employeeId}")
    public List<RequestLeaveDTO> getRequestsByEmployeeId(@PathVariable Long employeeId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        List<RequestLeaveDTO> requestLeaveDTOS = new ArrayList<>();
        List<RequestLeave> requestLeaves = requestLeaveService.getRequestsByEmployeeId(employeeId);
        for(RequestLeave requestLeave : requestLeaves){
            RequestLeaveDTO requestLeaveDTO = new RequestLeaveDTO();
            requestLeaveDTO.setId(requestLeave.getId());
            requestLeaveDTO.setApprove(requestLeave.getApprove());
            requestLeaveDTO.setStartTime(requestLeave.getStartTime().format(formatter));
            requestLeaveDTO.setEndTime(requestLeave.getEndTime().format(formatter));
            requestLeaveDTO.setReason(requestLeave.getReason());
            requestLeaveDTO.setName(requestLeave.getEmployeeInfo().getFullName());
            requestLeaveDTO.setEmployee_id(employeeId);
            requestLeaveDTOS.add(requestLeaveDTO);
        }
        return requestLeaveDTOS;
    }

    @PostMapping
    public RequestLeave createRequest(@RequestBody RequestLeaveDTO requestLeaveDTO) {
        RequestLeave requestLeave = new RequestLeave();
        EmployeeInfo employeeInfo = employeeInfoService.getEmployeeById(requestLeaveDTO.getEmployee_id());
        requestLeave.setEmployeeInfo(employeeInfo);
        System.out.println(requestLeaveDTO.getStartTime());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(requestLeaveDTO.getStartTime(), formatter);
        requestLeave.setStartTime(startTime);
        LocalDateTime endTime = LocalDateTime.parse(requestLeaveDTO.getEndTime(), formatter);
        requestLeave.setEndTime(endTime);
        requestLeave.setReason(requestLeaveDTO.getReason());
        return requestLeaveService.createRequest(requestLeave);
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approveRequest(@PathVariable Long id) {
        System.out.println("Request ID: " + id);
        RequestLeave requestLeave = requestLeaveService.findRequestLeave(id);

        if (requestLeave == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("RequestLeave with ID " + id + " not found");
        }

        requestLeave.setApprove(2);
        RequestLeave updatedRequestLeave = requestLeaveService.updateRequest(requestLeave);

        return ResponseEntity.ok(updatedRequestLeave);
    }

    @PutMapping("/approvepaidleave/{id}")
    public ResponseEntity<?> approveRequestpaidleave(@PathVariable Long id) {
        System.out.println("Request ID: " + id);
        RequestLeave requestLeave = requestLeaveService.findRequestLeave(id);
        LocalDateTime startTime = requestLeave.getStartTime();
        LocalDateTime endTime = requestLeave.getEndTime();
        LocalDate currentDate = startTime.toLocalDate();
        double totalLeaveHours = 0;
        // Nếu yêu cầu nghỉ phép rơi vào ngày hiện tại
        if (startTime.toLocalDate().isEqual(currentDate) && endTime.toLocalDate().isEqual(currentDate)) {
            // Tính giờ nghỉ trong ngày
            LocalDateTime leaveStart = startTime.isAfter(currentDate.atStartOfDay()) ? startTime : currentDate.atStartOfDay();
            LocalDateTime leaveEnd = endTime.isBefore(currentDate.atTime(23, 59)) ? endTime : currentDate.atTime(23, 59);
            long diffMinutes = ChronoUnit.MINUTES.between(leaveStart, leaveEnd); // Tính sự chênh lệch theo phút
            double diffHours = diffMinutes / 60.0; // Chuyển đổi thành giờ chính xác (có phần thập phân)
            // Tính số giờ nghỉ trong khoảng thời gian từ leaveStart đến leaveEnd
            totalLeaveHours += diffHours;
        }
        if(requestLeaveService.CheckOutOfTimeForMonth(requestLeave.getEmployeeInfo().getEmployeeId(),currentDate, totalLeaveHours)){
            return ResponseEntity.badRequest().build();
        }
        if (requestLeave == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("RequestLeave with ID " + id + " not found");
        }

        requestLeave.setApprove(1);
        RequestLeave updatedRequestLeave = requestLeaveService.updateRequest(requestLeave);

        return ResponseEntity.ok(updatedRequestLeave);
    }

    @PutMapping("/reject/{id}")
    public  RequestLeave rejectRequest(@PathVariable Long id){
        RequestLeave requestLeave = requestLeaveService.findRequestLeave(id);
        if(requestLeave!= null){
            requestLeave.setApprove(3);
        }
        return requestLeaveService.updateRequest(requestLeave);
    }


    @GetMapping("/is-on-leave")
    public ResponseEntity<Boolean> checkIfOnLeave(@RequestParam Long employeeId) {
        boolean isOnLeave = requestLeaveService.isCurrentlyOnApprovedLeave(employeeId);
        return ResponseEntity.ok(isOnLeave);
    }

    @GetMapping("check-have-requset-leaveunapproved")
    public ResponseEntity<Boolean> checkHaveRequsetLeaveunapproved(){
        Boolean check = requestLeaveService.getUnApprovedLeaves();
        return ResponseEntity.ok(check);
    }
    @GetMapping("date/{id}")
    public ResponseEntity<List<RequestLeaveDTO>> getRequestLeaveByDateandEmployeeId(  @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @PathVariable Long id){
        List<RequestLeaveDTO> requestLeaveDTOS = new ArrayList<>();
        List<RequestLeave> requestLeaves = requestLeaveService.getByDateAndEmployeeId(date,id);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        for(RequestLeave requestLeave : requestLeaves){
            RequestLeaveDTO requestLeaveDTO = new RequestLeaveDTO();
            requestLeaveDTO.setEmployee_id(requestLeave.getEmployeeInfo().getEmployeeId());
            requestLeaveDTO.setApprove(requestLeave.getApprove());
            requestLeaveDTO.setReason(requestLeave.getReason());
            requestLeaveDTO.setEndTime(requestLeave.getEndTime().format(formatter));
            requestLeaveDTO.setStartTime(requestLeave.getStartTime().format(formatter));
            requestLeaveDTO.setName(requestLeave.getEmployeeInfo().getFullName());
            requestLeaveDTOS.add(requestLeaveDTO);
        }

        return ResponseEntity.ok(requestLeaveDTOS);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RequestLeaveDTO> updateRequestLeave(@PathVariable Long id, @RequestBody RequestLeaveDTO requestLeaveDTO){
        System.out.println(requestLeaveDTO.toString());
        RequestLeave requestLeave = requestLeaveService.findRequestLeave(id);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        if(requestLeave != null){
            requestLeave.setEndTime(LocalDateTime.parse(requestLeaveDTO.getEndTime(),formatter));
            requestLeave.setStartTime(LocalDateTime.parse(requestLeaveDTO.getStartTime(),formatter));
            requestLeave.setReason(requestLeaveDTO.getReason());
            requestLeaveService.createRequest(requestLeave);
            return ResponseEntity.ok(requestLeaveDTO);
        }
        else{
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRequestLeave(@PathVariable Long id){
        RequestLeave requestLeave = requestLeaveService.findRequestLeave(id);
        requestLeaveService.deleteById(id);
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/checkOutOfTime/{employeeId}")
    public ResponseEntity<String> checkOutOfDate(@PathVariable Long employeeId){
        LocalDate date = LocalDate.now();
        Boolean check = requestLeaveService.CheckOutOfTimeForMonth(employeeId,date);
        if(!check){
            return ResponseEntity.ok("true");
        }
        else{
            return ResponseEntity.ok("false");
        }
    }
}
