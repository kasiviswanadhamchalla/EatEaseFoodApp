package com.eatease.user.controller;

import com.eatease.common.constants.Role;
import com.eatease.user.dto.*;
import com.eatease.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController controller;

    @Test
    void loginShouldReturnServiceResponse() {

        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("secret");
        AuthResponse expected = new AuthResponse("token", "user@example.com", 1L, List.of("CUSTOMER"), "User", true);
        when(userService.login(request)).thenReturn(expected);

        ResponseEntity<AuthResponse> response = controller.login(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }

    @Test
    void registerShouldReturnServiceResponse() {

        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@example.com");
        request.setPassword("secret1");
        request.setName("New");
        request.setRoles(Set.of(Role.CUSTOMER));
        AuthResponse expected = new AuthResponse("token", "new@example.com", 2L, List.of("CUSTOMER"), "New", true);
        when(userService.register(request)).thenReturn(expected);

        ResponseEntity<AuthResponse> response = controller.register(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }

    @Test
    void sendOtpShouldReturnOkAndInvokeService() {

        SendOtpRequest request = new SendOtpRequest();
        request.setEmail("otp@example.com");

        ResponseEntity<Void> response = controller.sendOtp(request);

        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(userService).sendRegisterOtp("otp@example.com");
    }
}
