package com.eatease.user.controller;

import com.eatease.user.dto.*;
import com.eatease.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/register/send-otp")
    public ResponseEntity<Void> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        userService.sendRegisterOtp(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtpAndRegister(
            @Valid @RequestBody VerifyOtpRegisterRequest request) {
        return ResponseEntity.ok(userService.verifyOtpAndRegister(request));
    }
}
