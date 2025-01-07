package com.example.demo.service;

import com.example.demo.model.RequestLeave;
import com.example.demo.repository.RequestLeaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestLeaveService {
    @Autowired
    private RequestLeaveRepository requestLeaveRepository;

    public RequestLeave findRequestLeave(Long id){
        return requestLeaveRepository.findById(id).orElse(null);
    }

    public List<RequestLeave> getAllRequests() {
        return requestLeaveRepository.findAllByOrderByStartTimeDesc();
    }

    public List<RequestLeave> getRequestsByEmployeeId(Long employeeId) {
        return requestLeaveRepository.findAllByEmployeeInfo_EmployeeId(employeeId);
    }

    public RequestLeave createRequest(RequestLeave requestLeave) {
        return requestLeaveRepository.save(requestLeave);
    }

    public RequestLeave updateRequest(RequestLeave requestLeave) {
        return requestLeaveRepository.save(requestLeave);
    }
    public List<RequestLeave> getApprovedLeaves(Long employeeId, int month, int year) {
        return requestLeaveRepository.getApprovedLeavesByEmployeeInfo_EmployeeId(employeeId).stream()
                .filter(leave -> leave.getStartTime().getMonthValue() == month && leave.getStartTime().getYear() == year)
                .collect(Collectors.toList());
    }

    public boolean isCurrentlyOnApprovedLeave(Long employeeId) {
        // Lấy danh sách các yêu cầu nghỉ phép đã phê duyệt của người dùng
        List<RequestLeave> approvedLeaves = requestLeaveRepository.getApprovedLeavesByEmployeeInfo_EmployeeId(employeeId);
        LocalDateTime now = LocalDateTime.now();

        // Kiểm tra xem thời gian hiện tại có nằm trong bất kỳ khoảng thời gian nào không
        for (RequestLeave leave : approvedLeaves) {
            if (now.isAfter(leave.getStartTime()) && now.isBefore(leave.getEndTime())) {
                return true;
            }
        }

        return false;
    }

    public double getLeaveHoursForDay(Long employeeId, LocalDate currentDate) {
        // Lấy danh sách các yêu cầu nghỉ phép đã phê duyệt của người dùng
        List<RequestLeave> approvedLeaves = requestLeaveRepository.getApprovedLeavesByEmployeeInfo_EmployeeId(employeeId);

        double totalLeaveHours = 0;

        for (RequestLeave leave : approvedLeaves) {
            // Kiểm tra xem yêu cầu nghỉ phép có thuộc ngày hiện tại hay không
            LocalDateTime startTime = leave.getStartTime();
            LocalDateTime endTime = leave.getEndTime();

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
        }

        return totalLeaveHours;
    }

    public Boolean getUnApprovedLeaves(){
        List<RequestLeave> requestLeaves = requestLeaveRepository.getUnApprovedLeaves();
        return !requestLeaves.isEmpty();
    }

    public List<RequestLeave> getByDateAndEmployeeId(LocalDate date, Long employeeId){
        LocalDateTime startOfDate = date.atStartOfDay();
        return requestLeaveRepository.findByDateAndEmployeeId(startOfDate, employeeId);
    }

    public void deleteById(Long id){
        requestLeaveRepository.deleteById(id);
    }

    public Boolean CheckOutOfTimeForMonth(Long employeeId, LocalDate curDate) {
        // Tính tháng và năm từ curDate
        int month = curDate.getMonthValue();
        int year = curDate.getYear();

        // Lấy số ngày trong tháng
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();

        // Tổng số giờ nghỉ trong tháng
        double totalLeaveHoursForMonth = 0;

        // Duyệt qua từng ngày của tháng
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate currentDate = LocalDate.of(year, month, day); // Tạo ngày hiện tại
            double leaveHours = getLeaveHoursForDay(employeeId, currentDate); // Gọi hàm tính giờ nghỉ trong ngày
            totalLeaveHoursForMonth += leaveHours; // Cộng dồn giờ nghỉ
        }

        // Kiểm tra nếu tổng giờ nghỉ > 9
        return totalLeaveHoursForMonth > 9;
    }

    public Boolean CheckOutOfTimeForMonth(Long employeeId, LocalDate curDate, double hour) {
        // Tính tháng và năm từ curDate
        int month = curDate.getMonthValue();
        int year = curDate.getYear();

        // Lấy số ngày trong tháng
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();

        // Tổng số giờ nghỉ trong tháng
        double totalLeaveHoursForMonth = 0;

        // Duyệt qua từng ngày của tháng
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate currentDate = LocalDate.of(year, month, day); // Tạo ngày hiện tại
            double leaveHours = getLeaveHoursForDay(employeeId, currentDate); // Gọi hàm tính giờ nghỉ trong ngày
            totalLeaveHoursForMonth += leaveHours; // Cộng dồn giờ nghỉ
        }

        // Kiểm tra nếu tổng giờ nghỉ > 9
        return totalLeaveHoursForMonth + hour> 9;
    }
}
