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
    private EmployeeInfoService employeeInfoService;
    @Autowired
    private RequestLeaveService requestLeaveService;
    @Autowired
    private SalaryService salaryService;
    @Autowired
    private MonthlySalaryService monthlySalaryService;

    @GetMapping("/hasCheckedInToday/{userId}")
    public ResponseEntity<String> hasCheckedInToday(@PathVariable Long userId){
        if(attendanceService.hasCheckedInToday(userId)){
            return ResponseEntity.ok("True");
        }
        else {
            return ResponseEntity.status(400).body("false");
        }
    }

    @GetMapping("/checkCheckinToday/{employee_id}")
    public  ResponseEntity<AttendanceDTO> checkCheckInToDay(@PathVariable Long employee_id){
        AttendanceDTO attendanceDTO = attendanceService.checkCheckInToday(employee_id);
        if(attendanceDTO == null){
            return  ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(attendanceService.checkCheckInToday(employee_id));
    }



    // Endpoint để lấy lịch sử chấm công của người dùng
    @GetMapping("/user/{EmployeeInfo}")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceByUserId(@PathVariable Long EmployeeInfo, @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,@RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        System.out.println(EmployeeInfo);
        System.out.println(startDate);
        System.out.println(endDate);
        List<Attendance> attendances = attendanceService.getAttendanceByUserAndDate(EmployeeInfo, startDate, endDate);
        if (attendances.isEmpty()) {
            return ResponseEntity.noContent().build(); // Trả về 204 nếu không có dữ liệu
        }
        List<AttendanceDTO> attendanceDTOS = new ArrayList<>();
        for(Attendance attendance: attendances){
            AttendanceDTO attendanceDTO = new AttendanceDTO();
            attendanceDTO.setId(attendance.getId());
            attendanceDTO.setUserId(attendance.getEmployeeInfo().getUser().getId());
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
            attendanceDTO.setFaceIn((attendance.getFaceIn() == null || attendance.getFaceIn().length == 0) ? null : attendance.getFaceIn());
            attendanceDTO.setFaceOut((attendance.getFaceOut() == null || attendance.getFaceOut().length == 0) ? null : attendance.getFaceOut());
            attendanceDTOS.add(attendanceDTO);
        }
        return ResponseEntity.ok(attendanceDTOS);
    }

    @GetMapping("/salary")
    public double getTotalHoursWorked(@RequestParam Long employeeId, @RequestParam int year, @RequestParam int month) {
        double totalHours = attendanceService.getTotalHoursWorkedInMonth(employeeId, year, Month.of(month));
        double totalPenaltyHours = attendanceService.calculatePenaltyHours(employeeId,month,year);
        double salary = 0;
        Boolean checkExist = employeeInfoService.checkExist(employeeId);
        if(checkExist){
            EmployeeInfo employeeInfo = employeeInfoService.findByUserId(employeeId);
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
        return attendanceService.getTotalHoursWorkedInMonth(employeeId, year, Month.of(month));
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
            Long userId = employee.getEmployeeId();
            String position = employee.getPosition() == 0 ? "Nhân viên" :
                    employee.getPosition() == 1 ? "Trưởng phòng" : "Quản lý";

            List<LocalDate> workingDays = getWorkingDays(month, year);
            List<Map<String, Object>> dailyDetails = new ArrayList<>(); // Danh sách chi tiết công việc mỗi ngày
            double totalLeaveHours = 0;
            for (LocalDate workDate : workingDays) {
                double totalDeduction = 0;
                double leaveHours = requestLeaveService.getLeaveHoursForDay(employee.getEmployeeId(), workDate);
                double effectiveHours = 0; // Số giờ làm thực tế
                boolean hasAttendance = false;

                Attendance attendance = attendanceService.getAttendanceByDate(userId, workDate);
                if (attendance != null && attendance.getCheckIn() != null && attendance.getCheckOut() != null) {
                    hasAttendance = true;
                    LocalDateTime checkIn = attendance.getCheckIn();
                    LocalDateTime checkOut = attendance.getCheckOut();

                    if (checkIn.getHour() < 8) {
                        checkIn = checkIn.withHour(8).withMinute(0).withSecond(0).withNano(0);
                    }
                    if (checkOut.getHour() >= 17) {
                        checkOut = checkOut.withHour(17).withMinute(0).withSecond(0).withNano(0);
                    }
                    long diffMinutes = ChronoUnit.MINUTES.between(checkIn, checkOut);
                    effectiveHours = diffMinutes / 60.0;
                }
                double ngayCong = 0;
                double tienPhat = 0;
                double time = effectiveHours + leaveHours;
                if(time > 6){
                    ngayCong += 1;
                }
                else if (time > 4) {
                    ngayCong +=  0.5;
                }
                double tmp =  9 - time;
                if(tmp > 0.5 && tmp <= 1){
                    tienPhat = 0.05;
                } else if (tmp > 1 && tmp <=2) {
                    tienPhat = 0.1;
                } else if (tmp > 2 && tmp <= 3) {
                    tienPhat = 0.2;
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");


                // Ghi lại thông tin chi tiết của ngày làm việc
                Map<String, Object> dailyDetail = new HashMap<>();
                dailyDetail.put("ngày", workDate);
                if((attendance != null && attendance.getCheckIn() != null)){
                    dailyDetail.put("Thời gian chấm công vào",attendance.getCheckIn().format(formatter));
                }
                else{
                    dailyDetail.put("Thời gian chấm công vào","");
                }
                if(attendance != null && attendance.getCheckOut() != null){
                    dailyDetail.put("thời gian chấm công ra", attendance.getCheckOut().format(formatter));
                }
                else{
                    dailyDetail.put("thời gian chấm công ra", "");
                }
                dailyDetail.put("ngày công", ngayCong);
                dailyDetail.put("tiền phạt", tienPhat);
                dailyDetails.add(dailyDetail);
            }

            // Lấy mức lương cơ bản từ Salary entity
            YearMonth yearMonth = YearMonth.of(year, month);
            LocalDate lastDayOfMonth = yearMonth.atEndOfMonth(); // Lấy ngày cuối cùng
            Salary salary = salaryService.findSalaryForMonth(userId, lastDayOfMonth);
            System.out.println(salary == null);
            double baseSalary = salary != null ? salary.getSalary() : 0;
            double cong  = 0;
            double tienPhat = 0;
            for (Map<String, Object> dailyDetail : dailyDetails) {
                Object ngayCongObj = dailyDetail.get("ngày công");
                if (ngayCongObj instanceof Number) {
                    cong += ((Number) ngayCongObj).doubleValue();
                }
                Object tienPhatObj = dailyDetail.get("tiền phạt");
                if (tienPhatObj instanceof  Number){
                    tienPhat += ((Number) tienPhatObj).doubleValue();
                }
            }
            double dailySalary = baseSalary / 22;
            double totalSalary = cong * dailySalary;
            // Thêm dữ liệu vào danh sách kết quả
            MonthlySalary monthlySalary = new MonthlySalary();
            monthlySalary.setEmployeeInfo(employee);
            MonthlySalaryId monthlySalaryId = new MonthlySalaryId();
            monthlySalaryId.setEmployeeId(employee.getEmployeeId());
            monthlySalaryId.setMonth(month);
            monthlySalaryId.setYear(year);
            monthlySalary.setId(monthlySalaryId);
            monthlySalary.setCreatedAt(LocalDateTime.now());
            monthlySalary.setNetSalary(totalSalary - (tienPhat * dailySalary));
            monthlySalary.setTotalPenalty(tienPhat * dailySalary);
            monthlySalary.setTotalWorkDays(cong);
//            if(!monthlySalaryService.checkIfMonthlySalaryExists(monthlySalaryId)){
////                monthlySalaryService.save(monthlySalary);
////            }
            monthlySalaryService.save(monthlySalary);
            Map<String, Object> record = new HashMap<>();
            record.put("userId", userId);
            record.put("Tên", employee.getFullName());
            record.put("Vị trí", position);
            record.put("month", month);
            record.put("year", year);
            record.put("Tổng lương", totalSalary);
            record.put("Tổng ngày công", cong);
            record.put("Tổng tiền phạt", tienPhat * dailySalary);
            record.put("Tiền thực nhận", totalSalary - (tienPhat * dailySalary));
            record.put("dailyDetails", dailyDetails); // Thêm chi tiết ngày làm việc
            if(!employee.getUser().isLocked()){
                salaryData.add(record);
            }
        }
        return salaryData;
    }
}
