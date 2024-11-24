package com.example.demo.service;

import com.example.demo.model.RequestLeave;
import com.example.demo.repository.RequestLeaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        return requestLeaveRepository.findAll();
    }

    public List<RequestLeave> getRequestsByUser(Long userId) {
        return requestLeaveRepository.findByUserId(userId);
    }

    public RequestLeave createRequest(RequestLeave requestLeave) {
        return requestLeaveRepository.save(requestLeave);
    }

    public RequestLeave updateRequest(RequestLeave requestLeave) {
        return requestLeaveRepository.save(requestLeave);
    }
    public List<RequestLeave> getApprovedLeaves(Long userId, int month, int year) {
        return requestLeaveRepository.getApprovedLeavesByUserId(userId).stream()
                .filter(leave -> leave.getStartTime().getMonthValue() == month && leave.getStartTime().getYear() == year)
                .collect(Collectors.toList());
    }

    public boolean isCurrentlyOnApprovedLeave(Long userId) {
        // Lấy danh sách các yêu cầu nghỉ phép đã phê duyệt của người dùng
        List<RequestLeave> approvedLeaves = requestLeaveRepository.getApprovedLeavesByUserId(userId);
        LocalDateTime now = LocalDateTime.now();

        // Kiểm tra xem thời gian hiện tại có nằm trong bất kỳ khoảng thời gian nào không
        for (RequestLeave leave : approvedLeaves) {
            if (now.isAfter(leave.getStartTime()) && now.isBefore(leave.getEndTime())) {
                return true;
            }
        }

        return false;
    }

    public double getLeaveHoursForDay(Long userId, LocalDate currentDate) {
        // Lấy danh sách các yêu cầu nghỉ phép đã phê duyệt của người dùng
        List<RequestLeave> approvedLeaves = requestLeaveRepository.getApprovedLeavesByUserId(userId);

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
}
