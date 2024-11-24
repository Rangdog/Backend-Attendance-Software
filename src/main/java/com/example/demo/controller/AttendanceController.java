package com.example.demo.controller;

import com.example.demo.DTO.AttendanceDTO;
import com.example.demo.model.*;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private AttendanceHistoryService attendanceHistoryService;
    @Autowired
    private EmployeeInfoService employeeInfoService;
    @Autowired
    private RequestLeaveService requestLeaveService;
    @Autowired
    private SalaryService salaryService;


    // Endpoint để check-in
//    @PostMapping("/checkin")
//    public ResponseEntity<Attendance> checkIn(@RequestBody Attendance attendance) {
//        attendance.setCheckIn(LocalDate.now().atStartOfDay()); // Thay đổi thời gian check-in
//        Attendance checkedInAttendance = attendanceService.checkIn(attendance);
//
//        // Lưu lịch sử chấm công
//        AttendanceHistory history = new AttendanceHistory();
//        history.setUser(attendance.getUser());
//        history.setAction(Action.CHECK_IN);
//        attendanceHistoryService.save(history);
//
//        return ResponseEntity.ok(checkedInAttendance);
//    }

    // Endpoint để check-out
//    @PostMapping("/checkout")
//    public ResponseEntity<Attendance> checkOut(@RequestBody Attendance attendance) {
//        attendance.setCheckOut(LocalDate.now().atStartOfDay()); // Thay đổi thời gian check-out
//        Attendance checkedOutAttendance = attendanceService.checkOut(attendance);
//
//        // Lưu lịch sử chấm công
//        AttendanceHistory history = new AttendanceHistory();
//        history.setUser(attendance.getUser());
//        history.setAction(Action.CHECK_OUT);
//        attendanceHistoryService.save(history);
//
//        return ResponseEntity.ok(checkedOutAttendance);
//    }

    @GetMapping("/hasCheckedInToday/{userId}")
    public ResponseEntity<String> hasCheckedInToday(@PathVariable Long userId){
        if(attendanceService.hasCheckedInToday(userId)){
            return ResponseEntity.ok("True");
        }
        else {
            return ResponseEntity.status(400).body("false");
        }
    }

    @GetMapping("/checkCheckinToday/{userId}")
    public  ResponseEntity<AttendanceDTO> checkCheckInToDay(@PathVariable Long userId){
        AttendanceDTO attendanceDTO = attendanceService.checkCheckInToday(userId);
        if(attendanceDTO == null){
            return  ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(attendanceService.checkCheckInToday(userId));
    }


    @PostMapping("/request-leave")
    public ResponseEntity<String> requestLeave(@RequestBody Attendance attendance) {
        AttendanceHistory history = new AttendanceHistory();
        history.setUser(attendance.getUser());
        history.setAction(Action.REQUEST_LEAVE);
        attendanceHistoryService.save(history);
        return ResponseEntity.ok("Leave request submitted.");
    }

    // Endpoint để lấy lịch sử chấm công của người dùng
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceByUserId(@PathVariable Long userId, @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,@RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<Attendance> attendances = attendanceService.getAttendanceByUserAndDate(userId, startDate, endDate);
        if (attendances.isEmpty()) {
            return ResponseEntity.noContent().build(); // Trả về 204 nếu không có dữ liệu
        }
        List<AttendanceDTO> attendanceDTOS = new ArrayList<>();
        for(Attendance attendance: attendances){
            AttendanceDTO attendanceDTO = new AttendanceDTO();
            attendanceDTO.setId(attendance.getId());
            attendanceDTO.setUserId(attendance.getUser().getId());
            if(attendance.getCheckIn() == null){
                attendanceDTO.setCheckIn("");
            }
            else {
                attendanceDTO.setCheckIn(attendance.getCheckIn().format(DATE_TIME_FORMATTER));
            }

            if(attendance.getCheckOut() == null){
                attendanceDTO.setCheckOut("");
            }
            else {
                attendanceDTO.setCheckOut(attendance.getCheckOut().format(DATE_TIME_FORMATTER));
            }
            if(attendance.getDate() == null){
                attendanceDTO.setDate("");
            }
            else{
                attendanceDTO.setDate(attendance.getDate().format(DATE_FORMATTER));
            }
            attendanceDTO.setFinish(attendance.isFinish());
            attendanceDTOS.add(attendanceDTO);
        }
        return ResponseEntity.ok(attendanceDTOS);
    }

    @GetMapping("/salary")
    public double getTotalHoursWorked(@RequestParam Long userId, @RequestParam int year, @RequestParam int month) {
        double totalHours = attendanceService.getTotalHoursWorkedInMonth(userId, year, Month.of(month));
        double totalPenaltyHours = attendanceService.calculatePenaltyHours(userId,month,year);
        double salary = 0;
        Boolean checkExist = employeeInfoService.checkExist(userId);
        if(checkExist){
            EmployeeInfo employeeInfo = employeeInfoService.findByUserId(userId);
            if(employeeInfo.getPosition() == 0){
                salary = (totalHours * 20000) - (totalPenaltyHours*20000);
            }
            else if(employeeInfo.getPosition() == 1){
                salary = (totalHours * 25000) - (totalPenaltyHours*25000);
            }
            else {
                salary = (totalHours * 30000) - (totalPenaltyHours*30000);
            }
        }
        return attendanceService.getTotalHoursWorkedInMonth(userId, year, Month.of(month));
    }

    public double calculateApprovedLeaveHours(Long userId, int month, int year) {
        List<RequestLeave> approvedLeaves = requestLeaveService.getApprovedLeaves(userId, month, year);
        double totalApprovedLeaveHours = 0;

        for (RequestLeave leave : approvedLeaves) {
            // Xác định khoảng thời gian nghỉ phép trong tháng hiện tại
            LocalDateTime start = leave.getStartTime();
            LocalDateTime end = leave.getEndTime();

            // Đảm bảo thời gian nằm trong tháng
            if (start.getMonthValue() == month && start.getYear() == year) {
                totalApprovedLeaveHours += Duration.between(start, end).toHours();
            }
        }

        return totalApprovedLeaveHours;
    }

    private List<LocalDate> getWorkingDays(int month, int year) {
        int totalDays = Month.of(month).length(Year.isLeap(year));
        List<LocalDate> workingDays = new ArrayList<>();

        for (int i = 1; i <= totalDays; i++) {
            LocalDate date = LocalDate.of(year, month, i);
            // Giả định thứ 7, chủ nhật không phải ngày làm việc
            if (!(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                workingDays.add(date);
            }
        }
        return workingDays;
    }

    private int getApprovedLeaveDays(Long userId, int month, int year) {
        List<RequestLeave> approvedLeaves = requestLeaveService.getApprovedLeaves(userId,month,year);
        int approvedDays = 0;

        for (RequestLeave leave : approvedLeaves) {
            LocalDate startDate = leave.getStartTime().toLocalDate();
            LocalDate endDate = leave.getEndTime().toLocalDate();

            // Lọc khoảng thời gian trong tháng đang xét
            if (startDate.getMonthValue() == month && startDate.getYear() == year) {
                approvedDays += (int) ChronoUnit.DAYS.between(startDate, endDate.plusDays(1));
            }
        }
        return approvedDays;
    }


    @GetMapping("/export")
    public List<Map<String, Object>> exportSalaries(@RequestParam int month, @RequestParam int year) {
        List<EmployeeInfo> allEmployees = employeeInfoService.getAllEmployee();
        List<Map<String, Object>> salaryData = new ArrayList<>();

        for (EmployeeInfo employee : allEmployees) {
            Long userId = employee.getUser().getId();
            String position = employee.getPosition() == 0 ? "Nhân viên" :
                    employee.getPosition() == 1 ? "Trưởng phòng" : "Quản lý";

            // Tổng số ngày làm việc trong tháng
            List<LocalDate> workingDays = getWorkingDays(month, year);
            double totalEffectiveDays = 0;  // Tổng công sau khi trừ muộn
              // Tổng trừ do muộn
            double sumLeaveHours = 0;
            boolean flagLeave = false;
            double cong = 22;
            System.out.println("user: " + userId);
            // Duyệt từng ngày làm việc trong tháng
            for (LocalDate workDate: workingDays) {
                double totalDeduction = 0;
                double leaveHours = requestLeaveService.getLeaveHoursForDay(userId,workDate);
                // Kiểm tra nếu nhân viên có ngày nghỉ phép đã được duyệt
                if(leaveHours == 9 && !flagLeave){
                    flagLeave = true;
                    totalEffectiveDays += 1;  // Nếu nghỉ phép, tính 1 công cho ngày đó
                    continue;
                }
                sumLeaveHours += leaveHours;

                // Kiểm tra chấm công
                Attendance attendance = attendanceService.getAttendanceByDate(userId, workDate);
                if (attendance != null && attendance.getCheckIn() != null && attendance.getCheckOut() != null) {
                    // Tính số giờ muộn
                    double lateHours = 0;
                    if(!flagLeave){
                        if(sumLeaveHours >= 9 && !flagLeave){
                            leaveHours -= (sumLeaveHours-9);
                            flagLeave = true;
                        }
                        LocalDateTime checkIn = attendance.getCheckIn();
                        LocalDateTime checkOut = attendance.getCheckOut();
                        if (attendance.getCheckIn().getHour() < 8) {
                            checkIn = checkIn.withHour(8).withMinute(0).withSecond(0).withNano(0);
                        }
                        if(attendance.getCheckOut().getHour() >= 17){
                            checkOut = checkOut.withHour(17).withMinute(0).withSecond(0).withNano(0);
                        }
                        long diffMinutes = ChronoUnit.MINUTES.between(checkIn, checkOut); // Tính sự chênh lệch theo phút
                        double diffHours = diffMinutes / 60.0; // Chuyển đổi thành giờ chính xác (có phần thập phân)
                        lateHours = 9 - leaveHours - diffHours;
                    }
                    else{
                        LocalDateTime checkIn = attendance.getCheckIn();
                        LocalDateTime checkOut = attendance.getCheckOut();
                        if (attendance.getCheckIn().getHour() < 8) {
                            checkIn = checkIn.withHour(8).withMinute(0).withSecond(0).withNano(0);
                        }
                        if(attendance.getCheckOut().getHour() >= 17){
                            checkOut = checkOut.withHour(17).withMinute(0).withSecond(0).withNano(0);
                        }
                        long diffMinutes = ChronoUnit.MINUTES.between(checkIn, checkOut); // Tính sự chênh lệch theo phút
                        double diffHours = diffMinutes / 60.0; // Chuyển đổi thành giờ chính xác (có phần thập phân)
                        lateHours = 9 - diffHours;
                    }
                    // Áp dụng các chính sách trừ lương do muộn
                    if (lateHours >= 0.5 && lateHours < 1) {
                        totalDeduction += 0.05; // Muộn > 30 phút, trừ 5% lương
                    } else if (lateHours >= 1 && lateHours <2) {
                        totalDeduction += 0.1;  // Muộn > 1 giờ, trừ 10% lương
                    } else if (lateHours >= 2 && lateHours < 4) {
                        totalDeduction += 0.2;  // Muộn > 2 giờ, trừ 20% lương
                    } else if (lateHours >= 4 && lateHours <=6) {
                        totalDeduction += 0.5;  // Muộn > 4 giờ, trừ 50% lương
                    } else if (lateHours > 6) {
                        continue;  // Muộn > 6 giờ, coi như không có công
                    }
                    cong -= totalDeduction; // Cộng vào số công trừ đi số trừ vì muộn
                }
                else{
                    if(leaveHours <= 2){
                        cong -= 1;
                    }
                    else if (leaveHours <= 5 ){
                        cong -= 0.5;
                    }
                    else if (leaveHours <= 7){
                        cong -= 0.1;
                    }
                }
            }
            LocalDate currentDate = LocalDate.now();
            // Lấy mức lương cơ bản từ Salary entity
            Salary salary = salaryService.findSalaryForMonth(userId, currentDate);
            double baseSalary = salary != null ? salary.getSalary() : 0;

            // Tính lương ngày
            double dailySalary = baseSalary / 22;  // Lương mỗi ngày là lương tháng chia cho 22 ngày

            // Tính tổng lương thực nhận
            double totalSalary = 0;
            if(cong > 0){
                 totalSalary = cong * dailySalary;
            }



            // Thêm dữ liệu vào danh sách kết quả
            Map<String, Object> record = new HashMap<>();
            record.put("userId", userId);
            record.put("name", employee.getFullName());
            record.put("position", position);
            record.put("month", month);
            record.put("year", year);
            record.put("effectiveDays", cong);
            record.put("totalSalary", totalSalary);
            salaryData.add(record);
        }

        return salaryData;
    }
}
