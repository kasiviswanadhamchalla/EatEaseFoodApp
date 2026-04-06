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
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailOtpRepository emailOtpRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserServiceImpl(UserRepository userRepository,
                           EmailOtpRepository emailOtpRepository,
                           EmailService emailService,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.emailOtpRepository = emailOtpRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // ===================== LOGIN =====================

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (!user.isEnabled()) {
            throw new UnauthorizedException("Account is disabled");
        }

        if (!user.isApproved() && !user.getRoles().contains(Role.ADMIN)) {
            throw new UnauthorizedException("Account pending admin approval");
        }

        List<String> roles = new ArrayList<>();
        for (Role role : user.getRoles()) {
            roles.add(role.name());
        }

        String token = jwtService.generateToken(user.getEmail(), user.getId(), roles);

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setEmail(user.getEmail());
        response.setUserId(user.getId());
        response.setRoles(roles);
        response.setName(user.getName());
        response.setApproved(user.isApproved());

        return response;
    }

    // ===================== NORMAL REGISTER (NON-CUSTOMER) =====================

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (request.getRoles().contains(Role.CUSTOMER)) {

            sendRegisterOtp(request.getEmail());

            return new AuthResponse(
                    null,
                    request.getEmail(),
                    null,
                    List.of("OTP_SENT"),
                    request.getName(),
                    false
            );
        }

        if (request.getRoles().contains(Role.ADMIN)) {
            throw new BadRequestException("Cannot self-register as ADMIN");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setRoles(request.getRoles());
        user.setEnabled(true);
        user.setApproved(false);

        userRepository.save(user);

        List<String> roles = new ArrayList<>();
        for (Role role : user.getRoles()) {
            roles.add(role.name());
        }

        String token = jwtService.generateToken(user.getEmail(), user.getId(), roles);

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getId(),
                roles,
                user.getName(),
                user.isApproved()
        );
    }

    // ===================== SEND OTP =====================

    @Transactional
    public void sendRegisterOtp(String email) {

        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already registered");
        }

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        EmailOtp emailOtp = new EmailOtp();
        emailOtp.setEmail(email);
        emailOtp.setOtp(otp);
        emailOtp.setExpiresAt(Instant.now().plusSeconds(300));
        emailOtp.setVerified(false);

        emailOtpRepository.save(emailOtp);
        emailService.sendOtpEmail(email, otp);
    }

    // ===================== VERIFY OTP + REGISTER CUSTOMER =====================

    @Transactional
    public AuthResponse verifyOtpAndRegister(VerifyOtpRegisterRequest request) {

        if (request.getRoles().contains(Role.ADMIN)) {
            throw new BadRequestException("Cannot register as ADMIN");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        EmailOtp emailOtp = emailOtpRepository.findTopByEmailOrderByIdDesc(request.getEmail())
                .orElseThrow(() -> new BadRequestException("OTP not found"));

        if (emailOtp.isVerified()) {
            throw new BadRequestException("OTP already used");
        }

        if (emailOtp.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("OTP expired");
        }

        if (!emailOtp.getOtp().equals(request.getOtp())) {
            throw new BadRequestException("Invalid OTP");
        }

        emailOtp.setVerified(true);
        emailOtpRepository.save(emailOtp);

        boolean autoApproved = request.getRoles().contains(Role.CUSTOMER);

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setRoles(request.getRoles());
        user.setEnabled(true);
        user.setApproved(autoApproved);

        userRepository.save(user);

        List<String> roles = new ArrayList<>();
        for (Role role : user.getRoles()) {
            roles.add(role.name());
        }

        String token = jwtService.generateToken(user.getEmail(), user.getId(), roles);

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setEmail(user.getEmail());
        response.setUserId(user.getId());
        response.setRoles(roles);
        response.setName(user.getName());
        response.setApproved(user.isApproved());

        return response;
    }

    // ===================== USER MANAGEMENT =====================

    @Override
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return toResponse(user);
    }

    @Override
    public List<UserResponse> findAll() {
        List<UserResponse> responses = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            responses.add(toResponse(user));
        }
        return responses;
    }

    @Override
    public List<UserResponse> findByRole(Role role) {
        List<UserResponse> responses = new ArrayList<>();
        for (User user : userRepository.findByRolesContaining(role)) {
            responses.add(toResponse(user));
        }
        return responses;
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getName() != null) user.setName(request.getName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getRoles() != null) user.setRoles(request.getRoles());
        if (request.getEnabled() != null) user.setEnabled(request.getEnabled());

        if (request.getApproved() != null && !user.getRoles().contains(Role.ADMIN)) {
            user.setApproved(request.getApproved());
        }

        userRepository.save(user);
        return toResponse(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public List<UserResponse> findPendingApproval() {
        List<UserResponse> responses = new ArrayList<>();
        for (User user : userRepository.findByApprovedFalse()) {
            responses.add(toResponse(user));
        }
        return responses;
    }

    @Override
    @Transactional
    public UserResponse approveUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setApproved(true);
        userRepository.save(user);
        return toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse revokeApproval(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (user.getRoles().contains(Role.ADMIN)) {
            throw new BadRequestException("Cannot revoke approval for ADMIN");
        }

        user.setApproved(false);
        userRepository.save(user);
        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                user.getRoles(),
                user.isEnabled(),
                user.isApproved(),
                user.getCreatedAt()
        );
    }
}
