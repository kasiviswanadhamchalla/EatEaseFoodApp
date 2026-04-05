package com.eatease.user.service;

import com.eatease.common.constants.Role;
import com.eatease.user.dto.*;

import java.util.List;

public interface UserService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
    UserResponse getById(Long id);
    List<UserResponse> findAll();
    List<UserResponse> findByRole(Role role);
    UserResponse update(Long id, UserUpdateRequest request);
    void delete(Long id);
    List<UserResponse> findPendingApproval();
    UserResponse approveUser(Long id);
    UserResponse revokeApproval(Long id);
    void sendRegisterOtp(String email);
    AuthResponse verifyOtpAndRegister(VerifyOtpRegisterRequest request);

}
