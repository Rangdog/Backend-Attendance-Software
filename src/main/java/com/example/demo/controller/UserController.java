package com.example.demo.controller;

import com.example.demo.DTO.CheckInResponse;
import com.example.demo.DTO.RecognitionResult;
import com.example.demo.model.Attendance;
import com.example.demo.model.User;
import com.example.demo.service.AttendanceService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("api/users")
public class UserController {
    @Value("${face.images.directory}")
    private String faceImagesDirectory;

    private final RestTemplate restTemplate;


    @Autowired
    private UserService userService;
    @Autowired
    private AttendanceService attendanceService;

    public UserController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Endpoint để tạo người dùng mới
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/register-face")
    public ResponseEntity<String> registerFace(@RequestParam("username") String username, @RequestParam("faceImage") MultipartFile faceImage){
        if (username == null || username.isEmpty() || faceImage.isEmpty()) {
            return ResponseEntity.badRequest().body("Username and image file are required.");
        }
        File directory = new File(faceImagesDirectory);
        if (!directory.exists()) {
            directory.mkdirs(); // Create the directory if it doesn't exist
        }
        String imagePath = directory.getAbsolutePath() + File.separator + faceImage.getOriginalFilename();
        try (FileOutputStream fos = new FileOutputStream(imagePath)) {
            fos.write(faceImage.getBytes());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to save the image: " + e.getMessage());
        }

        // Setting up the request to Flask
        String flaskUrl = "http://localhost:5000/register-face"; // Flask endpoint
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Prepare multipart form data
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("faceImage", new FileSystemResource(imagePath));
        body.add("username", username);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(flaskUrl, HttpMethod.POST, requestEntity, String.class);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to communicate with the Flask application: " + e.getMessage());
        }

        // Check the response status
        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok("Face registered successfully: " + username);
        } else {
            return ResponseEntity.status(response.getStatusCode()).body("Error registering face with model: " + response.getBody());
        }
    }

    @PostMapping("/check-in/{userId}")
    public ResponseEntity<?> checkIn(@RequestParam("faceImage") MultipartFile file, @PathVariable Long userId) throws IOException {
        String flaskUrl = "http://localhost:5000/recognize-face1"; // Flask endpoint
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ByteArrayResource byteArrayResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename(); // Giữ lại tên file gốc
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("faceImage", byteArrayResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<RecognitionResult> response = restTemplate.exchange(
                    flaskUrl,
                    HttpMethod.POST,
                    requestEntity,
                    RecognitionResult.class
            );
            System.out.println("Response JSON: " + response.getBody());

            RecognitionResult result = response.getBody();

            if (response.getStatusCode().is2xxSuccessful() && result != null) {
                if (result.isResult()) {
                    System.out.println(Objects.equals(result.getLabel(), String.valueOf(userId)));
                    if(Objects.equals(result.getLabel(), String.valueOf(userId))){
                        Attendance attendance = attendanceService.checkIn(userId);
                        CheckInResponse responseChecking = new CheckInResponse(result, attendance.getId());
                        return ResponseEntity.ok(responseChecking);
                    }
                    return ResponseEntity.status(401).body(result);
                } else {
                    return ResponseEntity.status(400).body(result);
                }
            } else {
                return ResponseEntity.status(response.getStatusCode())
                        .body("Unexpected response from Flask: " + response.getStatusCode());
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Xử lý lỗi HTTP cụ thể
            return ResponseEntity.status(e.getStatusCode())
                    .body("HTTP Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            // Xử lý lỗi khác
            e.printStackTrace();  // In stack trace để biết thêm chi tiết
            return ResponseEntity.status(500).body("Internal Error: " + e.getMessage());
        }
    }



    @PostMapping("/check-out/{userId}")
    public ResponseEntity<?> checkOut(@RequestParam("faceImage") MultipartFile file, @RequestParam("attendanceId") Long attendanceId, @PathVariable Long userId) throws IOException {
        if(!attendanceService.hasCheckedInToday(userId)){
            return ResponseEntity.status(400).body("User không có check in");
        }
        String flaskUrl = "http://localhost:5000/recognize-face1"; // Flask endpoint
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ByteArrayResource byteArrayResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename(); // Giữ lại tên file gốc
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("faceImage", byteArrayResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<RecognitionResult> response = restTemplate.exchange(
                    flaskUrl,
                    HttpMethod.POST,
                    requestEntity,
                    RecognitionResult.class
            );
            System.out.println("Response JSON: " + response.getBody());

            RecognitionResult result = response.getBody();

            if (response.getStatusCode().is2xxSuccessful() && result != null) {
                if (result.isResult()) {
                    if(Objects.equals(result.getLabel(), String.valueOf(userId))){
                        attendanceService.checkOut(userId, attendanceId);
                        return ResponseEntity.ok(result);
                    }
                    return ResponseEntity.status(401).body(result);
                } else {
                    return ResponseEntity.status(400).body(result);
                }
            } else {
                return ResponseEntity.status(response.getStatusCode())
                        .body("Unexpected response from Flask: " + response.getStatusCode());
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Xử lý lỗi HTTP cụ thể
            return ResponseEntity.status(e.getStatusCode())
                    .body("HTTP Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            // Xử lý lỗi khác
            e.printStackTrace();  // In stack trace để biết thêm chi tiết
            return ResponseEntity.status(500).body("Internal Error: " + e.getMessage());
        }
    }

    // Endpoint để lấy danh sách người dùng
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Endpoint để lấy thông tin người dùng theo username
    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }
}
