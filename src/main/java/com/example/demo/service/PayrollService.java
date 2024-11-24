package com.example.demo.service;

import com.example.demo.model.Attendance;
import com.example.demo.model.Payroll;
import com.example.demo.model.User;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.PayrollRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class PayrollService {
    @Autowired
    private PayrollRepository payrollRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;

    public Payroll calculatePayroll(Long userId, int month, int year) {
        List<Attendance> attendanceList = attendanceRepository.findByUserIdAndDateBetween(userId,
                LocalDate.of(year, month, 1),
                LocalDate.of(year, month, monthDays(month)));

        double totalHours = 0;
        double salary = 0;
        double penalties = 0;

        for (Attendance attendance : attendanceList) {
            if (attendance.getCheckIn() != null && attendance.getCheckOut() != null) {
                Duration workedDuration = Duration.between(attendance.getCheckIn(), attendance.getCheckOut());
                totalHours += workedDuration.toHours();

                // Giả sử lương theo giờ là 100. Tính lương
                salary += workedDuration.toHours() * 100;

                // Giả sử phạt nếu check-in muộn hơn 9:00 AM
                if (attendance.getCheckIn().toLocalTime().isAfter(LocalTime.of(9, 0))) {
                    penalties += 50; // Giả sử phạt là 50
                }
            }
        }
        User user = userRepository.findUserById(userId);
        Payroll payroll = new Payroll();
        payroll.setUser(user);
        payroll.setMonth(month);
        payroll.setYear(year);
        payroll.setTotalHours(totalHours);
        payroll.setSalary(salary);
        payroll.setPenalties(penalties);

        return payrollRepository.save(payroll);
    }

    // Phương thức lấy lương theo userId, month, year
    public Payroll getPayrollByUserIdAndMonthYear(Long userId, int month, int year) {
        Optional<Payroll> payroll = payrollRepository.findByUserIdAndMonthAndYear(userId, month, year);
        return payroll.orElse(null); // Trả về null nếu không tìm thấy
    }

    private int monthDays(int month) {
        return LocalDate.of(2023, month, 1).lengthOfMonth();
    }
}