package com.eatease.user.service;

import com.eatease.common.constants.Role;
import com.eatease.common.exception.BadRequestException;
import com.eatease.common.exception.ResourceNotFoundException;
import com.eatease.common.exception.UnauthorizedException;
import com.eatease.user.dto.*;
import com.eatease.user.entity.EmailOtp;
import com.eatease.user.entity.User;
import com.eatease.user.repository.EmailOtpRepository;
import com.eatease.user.repository.UserRepository;
import com.eatease.user.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailOtpRepository emailOtpRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void loginShouldReturnAuthResponseForApprovedUser() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("secret");
        User user = user(11L, "user@example.com", "hash", Set.of(Role.CUSTOMER), true, true);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "hash")).thenReturn(true);
        when(jwtService.generateToken(eq("user@example.com"), eq(11L), any())).thenReturn("jwt-token");

        AuthResponse response = userService.login(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals("user@example.com", response.getEmail());
        assertEquals(11L, response.getUserId());
        assertEquals(List.of("CUSTOMER"), response.getRoles());
    }

    @Test
    void loginShouldThrowWhenPasswordInvalid() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("wrong");
        User user = user(11L, "user@example.com", "hash", Set.of(Role.CUSTOMER), true, true);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hash")).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> userService.login(request));
    }

    @Test
    void registerShouldSendOtpForCustomerRole() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("customer@example.com");
        request.setPassword("secret123");
        request.setName("Customer");
        request.setRoles(Set.of(Role.CUSTOMER));
        when(userRepository.existsByEmail("customer@example.com")).thenReturn(false);

        AuthResponse response = userService.register(request);

        assertEquals("customer@example.com", response.getEmail());
        assertTrue(response.getRoles().contains("CUSTOMER"));
        assertEquals("OTP_SENT", response.getMessage());
        verify(emailOtpRepository).save(any(EmailOtp.class));
        verify(emailService).sendOtpEmail(eq("customer@example.com"), anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerShouldThrowWhenAdminRoleProvided() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("admin@example.com");
        request.setPassword("secret123");
        request.setName("Admin");
        request.setRoles(Set.of(Role.ADMIN));

        assertThrows(BadRequestException.class, () -> userService.register(request));
    }

    @Test
    void sendRegisterOtpShouldSaveOtpAndSendEmail() {
        when(userRepository.existsByEmail("otp@example.com")).thenReturn(false);

        userService.sendRegisterOtp("otp@example.com");

        verify(emailOtpRepository).save(any(EmailOtp.class));
        verify(emailService).sendOtpEmail(eq("otp@example.com"), anyString());
    }

    @Test
    void sendRegisterOtpShouldThrowWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("otp@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.sendRegisterOtp("otp@example.com"));
    }

    @Test
    void verifyOtpAndRegisterShouldCreateApprovedCustomer() {
        VerifyOtpRegisterRequest request = new VerifyOtpRegisterRequest();
        request.setEmail("new@example.com");
        request.setOtp("123456");
        request.setPassword("secret123");
        request.setName("New User");
        request.setRoles(Set.of(Role.CUSTOMER));
        EmailOtp otp = new EmailOtp();
        otp.setEmail("new@example.com");
        otp.setOtp("123456");
        otp.setExpiresAt(Instant.now().plusSeconds(60));
        otp.setVerified(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(emailOtpRepository.findTopByEmailOrderByIdDesc("new@example.com")).thenReturn(Optional.of(otp));
        when(passwordEncoder.encode("secret123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User toSave = invocation.getArgument(0);
            if (toSave.getId() == null) {
                toSave.setId(25L);
            }
            return toSave;
        });
        when(jwtService.generateToken(eq("new@example.com"), any(), any())).thenReturn("jwt-token");

        AuthResponse response = userService.verifyOtpAndRegister(request);

        assertEquals("jwt-token", response.getToken());
        assertTrue(response.isApproved());
        verify(emailOtpRepository).save(otp);
    }

    @Test
    void updateShouldThrowWhenUserNotFound() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.update(100L, new UserUpdateRequest()));
    }

    @Test
    void revokeApprovalShouldThrowForAdmin() {
        User admin = user(1L, "admin@example.com", "hash", Set.of(Role.ADMIN), true, true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        assertThrows(BadRequestException.class, () -> userService.revokeApproval(1L));
    }

    @Test
    void approveUserShouldSetApprovedTrue() {
        User user = user(9L, "u@example.com", "hash", Set.of(Role.ADMIN), true, false);
        when(userRepository.findById(9L)).thenReturn(Optional.of(user));

        UserResponse response = userService.approveUser(9L);

        assertTrue(response.isApproved());
        verify(userRepository).save(user);
    }

    private User user(Long id, String email, String hash, Set<Role> roles, boolean enabled, boolean approved) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPasswordHash(hash);
        user.setName("Test");
        user.setPhone("1234567890");
        user.setRoles(roles);
        user.setEnabled(enabled);
        user.setApproved(approved);
        return user;
    }
}
