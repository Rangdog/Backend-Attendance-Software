package com.example.demo.service;

import com.example.demo.DTO.AttendanceDTO;
import com.example.demo.model.Attendance;
import com.example.demo.model.EmployeeInfo;
import com.example.demo.model.User;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.EmployeeInfoRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
public class AttendanceService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmployeeInfoRepository employeeInfoRepository;
    public Attendance save(Attendance attendance){
        return attendanceRepository.save(attendance);
    }
    public Attendance checkIn(Long employee_id, byte[] faceIn) {
        Attendance attendance = new Attendance();
        EmployeeInfo employeeInfo = employeeInfoRepository.findById(employee_id).orElse(null);
        attendance.setEmployeeInfo(employeeInfo);
        attendance.setCheckIn(new Timestamp(System.currentTimeMillis()).toLocalDateTime());
        attendance.setDate(LocalDate.now());
        attendance.setFaceIn(faceIn);
        attendanceRepository.save(attendance);
        return attendance;
    }

    public Attendance checkOut(Long userId, Long attendanceId, byte[] faceOut) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("No check-in record found for user."));
        attendance.setCheckOut(new Timestamp(System.currentTimeMillis()).toLocalDateTime());
        attendance.setFaceOut(faceOut);
        return attendanceRepository.save(attendance);
    }
    public List<Attendance> getAttendanceByUserAndDate(Long EmployeeInfo, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByEmployeeIdAndDateBetween(EmployeeInfo, startDate.plusDays(1), endDate.plusDays(1));
    }

    // Lấy tất cả lịch sử chấm công của user

    public List<Attendance> getAttendanceByUserId(Long userId) {
        // Thêm logic để lấy dữ liệu chấm công theo userId
        return null;
    }

    public boolean hasCheckedInToday(Long userId) {
        LocalDate today = LocalDate.now();
        return attendanceRepository.existsByEmployeeInfo_EmployeeIdAndDate(userId, today);
    }

    public AttendanceDTO checkCheckInToday(Long employee_id){
        LocalDate today = LocalDate.now();
        System.out.println(today.format(DATE_FORMATTER));
        Attendance attendance = attendanceRepository.findFirstByEmployeeInfo_EmployeeIdAndDate(employee_id,today);
        AttendanceDTO attendanceDTO = new AttendanceDTO();
        if(attendance == null){
            return null;
        }
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
        return attendanceDTO;
    }

    public double getTotalHoursWorkedInMonth(Long employeeId, int year, Month month) {
        // Lấy ngày bắt đầu và ngày kết thúc của tháng
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // Lấy danh sách bản ghi chấm công trong tháng
        List<Attendance> attendanceRecords = attendanceRepository.findByEmployeeIdAndDateBetween(employeeId, startDate, endDate);

        // Tính tổng số giờ
        double totalHours = 0;
        for (Attendance record : attendanceRecords) {
            if (record.getCheckIn() != null && record.getCheckOut() != null) {
                // Tính thời gian làm việc của từng bản ghi
                double hoursWorked = java.time.Duration.between(record.getCheckIn(), record.getCheckOut()).toMinutes() / 60.0;
                totalHours += hoursWorked;
            }
        }

        return totalHours;
    }

    public double calculatePenaltyHours(Long employeeId, int month, int year) {
        // Lấy danh sách record trong tháng của user
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<Attendance> attendanceRecords = attendanceRepository.findByEmployeeIdAndDateBetween(employeeId, startDate, endDate);

        // Biến lưu tổng giờ phạt
        double totalPenaltyHours = 0;

        // Giờ quy định (08:00 - 17:00)
        LocalTime workStart = LocalTime.of(8, 0);
        LocalTime workEnd = LocalTime.of(17, 0);

        // Xử lý từng bản ghi
        for (Attendance attendance : attendanceRecords) {
            // Chỉ tính các record có cả check_in và check_out
            if (attendance.getCheckIn() != null && attendance.getCheckOut() != null) {
                LocalDateTime checkIn = attendance.getCheckIn();
                LocalDateTime checkOut = attendance.getCheckOut();

                // Tính giờ phạt do check_in
                if (checkIn.toLocalTime().isAfter(workStart)) { // Check-in muộn
                    double penaltyForCheckIn = java.time.Duration.between(workStart, checkIn.toLocalTime()).toMinutes() / 60.0;
                    totalPenaltyHours += penaltyForCheckIn;
                }

                // Tính giờ phạt do check_out
                if (checkOut.toLocalTime().isBefore(workEnd)) { // Check-out sớm
                    double penaltyForCheckOut = java.time.Duration.between(checkOut.toLocalTime(), workEnd).toMinutes() / 60.0;
                    totalPenaltyHours += penaltyForCheckOut;
                }
            }
        }

        return totalPenaltyHours;
    }

//    public int getAttendanceDays(Long userId, int month, int year) {
//        List<Attendance> attendanceRecords = attendanceRepository.findAttendanceInMonthAndYear(userId, month, year);
//        return attendanceRecords.size(); // Số ngày chấm công
//    }

    public Attendance getAttendanceByDate(Long employee_id, LocalDate currentDate){
        return attendanceRepository.findFirstByEmployeeInfo_EmployeeIdAndDate(employee_id,currentDate);
    }
}
