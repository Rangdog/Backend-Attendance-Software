package com.example.demo.controller;

import com.example.demo.DTO.AuthRequest;
import com.example.demo.DTO.AuthResponse;
import com.example.demo.model.EmployeeInfo;
import com.example.demo.model.User;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.EmployeeInfoService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private EmployeeInfoService employeeInfoService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
            User user = userService.findByUsername(userDetails.getUsername());
            EmployeeInfo employeeInfo = employeeInfoService.findByUserId(user.getId());
            final String jwt = jwtUtil.generateToken(userDetails.getUsername(), userDetails.getAuthorities().toString(),user.getId(), employeeInfo.getEmployeeId() );
            System.out.println(jwt);
            return ResponseEntity.ok(new AuthResponse(jwt));
        } catch (Exception e) {
            e.printStackTrace(); // In ra stack trace để kiểm tra lỗi
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestBody String token) {
        try {
            // Kiểm tra tính hợp lệ của token
            String username = jwtUtil.extractUsername(token);
            if (username == null || username.isEmpty()) {
                return ResponseEntity.status(400).body("Invalid token");
            }

            // Load UserDetails từ username để kiểm tra tính hợp lệ
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            boolean isValid = jwtUtil.validateToken(token, userDetails);

            if (isValid) {
                return ResponseEntity.ok("Token is valid");
            } else {
                return ResponseEntity.status(401).body("Token is invalid");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log lỗi để kiểm tra
            return ResponseEntity.status(401).body("Token validation failed");
        }
    }

    @PostMapping("/login/face")
    public String loginWithFace(@RequestBody byte[] faceData) {
        // Logic để xác thực bằng khuôn mặt
        // 1. Tìm user bằng dữ liệu khuôn mặt.
        // 2. Nếu tìm thấy, trả về JWT token.
        return "Face login not yet implemented"; // Chỗ này cần triển khai
    }
}
