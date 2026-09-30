package com.library.controller;

import com.library.service.AuthService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(
            @RequestParam @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email) {

        String response = authService.sendOtp(email);

        if (response.equals("OTP sent successfully")) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(
            @RequestParam @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,

            @RequestParam @NotBlank(message = "OTP is required") String otp) {

        String response = authService.verifyOtp(email, otp);

        if (response.startsWith("OTP verified")) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().body(response);
    }
}