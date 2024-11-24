package com.example.demo.controller;

import com.example.demo.DTO.RequestLeaveDTO;
import com.example.demo.model.Attendance;
import com.example.demo.model.RequestLeave;
import com.example.demo.model.User;
import com.example.demo.service.AttendanceService;
import com.example.demo.service.RequestLeaveService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
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

    @GetMapping
    public List<RequestLeaveDTO> getALLRequestLeave(){
        List<RequestLeaveDTO> leaveDTOS = new ArrayList<>();
        List<RequestLeave> leaves = requestLeaveService.getAllRequests();
        for (RequestLeave leave : leaves){
            RequestLeaveDTO requestLeaveDTO = new RequestLeaveDTO();
            requestLeaveDTO.setUserName(leave.getUser().getUserName());
            requestLeaveDTO.setStartTime(leave.getStartTime().format(formatter));
            requestLeaveDTO.setEndTime(leave.getEndTime().format(formatter));
            requestLeaveDTO.setApprove(leave.getApprove());
            requestLeaveDTO.setReason(leave.getReason());
            requestLeaveDTO.setId(leave.getId());
            leaveDTOS.add(requestLeaveDTO);
        }
        return leaveDTOS;
    }

    @GetMapping("/user/{userId}")
    public List<RequestLeave> getRequestsByUser(@PathVariable Long userId) {

        return requestLeaveService.getRequestsByUser(userId);
    }

    @PostMapping
    public RequestLeave createRequest(@RequestBody RequestLeaveDTO requestLeaveDTO) {
        RequestLeave requestLeave = new RequestLeave();
        User user = userService.findById(requestLeaveDTO.getUserId());
        requestLeave.setUser(user);
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(requestLeaveDTO.getStartTime());
        LocalDateTime startTime = offsetDateTime.toLocalDateTime();
        requestLeave.setStartTime(startTime);
        offsetDateTime = OffsetDateTime.parse(requestLeaveDTO.getEndTime());
        LocalDateTime endTime = offsetDateTime.toLocalDateTime();
        requestLeave.setEndTime(endTime);
        requestLeave.setReason(requestLeaveDTO.getReason());
        if(attendanceService.hasCheckedInToday(user.getId())){
            LocalDate current = LocalDate.now();
            LocalDateTime now = LocalDateTime.now();
            Attendance attendance = attendanceService.getAttendanceByDate(user.getId(),current);
            if (attendance.getCheckOut() != null){
                attendance.setCheckOut(now);
            }
            attendanceService.save(attendance);
        }
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

        requestLeave.setApprove(true);
        RequestLeave updatedRequestLeave = requestLeaveService.updateRequest(requestLeave);

        return ResponseEntity.ok(updatedRequestLeave);
    }

    @PutMapping("/reject/{id}")
    public  RequestLeave rejectRequest(@PathVariable Long id){
        RequestLeave requestLeave = requestLeaveService.findRequestLeave(id);
        if(requestLeave!= null){
            requestLeave.setApprove(false);
        }
        return requestLeaveService.updateRequest(requestLeave);
    }

    @PutMapping("/{id}")
    public RequestLeave updateRequest(@PathVariable Long id, @RequestBody RequestLeave requestLeave) {
        requestLeave.setId(id);
        return requestLeaveService.updateRequest(requestLeave);
    }

    @GetMapping("/is-on-leave")
    public ResponseEntity<Boolean> checkIfOnLeave(@RequestParam Long userId) {
        boolean isOnLeave = requestLeaveService.isCurrentlyOnApprovedLeave(userId);
        return ResponseEntity.ok(isOnLeave);
    }
}
