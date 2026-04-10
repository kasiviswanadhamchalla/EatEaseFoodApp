package com.eatease.user.controller;

import com.eatease.common.constants.Role;
import com.eatease.user.dto.UserResponse;
import com.eatease.user.dto.UserUpdateRequest;
import com.eatease.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Clear security context
        SecurityContextHolder.clearContext();
    }

    @Test
    void getById_ShouldReturnUserResponse_WhenValidId() {
        // Arrange
        Long userId = 1L;
        UserResponse expectedResponse = new UserResponse(userId, "test@example.com",
                "Test User", "+1234567890", Arrays.asList(Role.CUSTOMER),
                true, true, "2026-01-01T00:00:00Z");

        when(userService.getById(userId)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<UserResponse> response = userController.getById(userId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(userService, times(1)).getById(userId);
    }

    @Test
    void findAll_ShouldReturnListOfUsers_WhenAdmin() {
        // Arrange
        UserResponse user1 = new UserResponse(1L, "user1@example.com",
                "User One", "+1234567890", Arrays.asList(Role.CUSTOMER),
                true, true, "2026-01-01T00:00:00Z");
        UserResponse user2 = new UserResponse(2L, "user2@example.com",
                "User Two", "+0987654321", Arrays.asList(Role.ADMIN),
                true, true, "2026-01-02T00:00:00Z");
        List<UserResponse> expectedUsers = Arrays.asList(user1, user2);

        when(userService.findAll()).thenReturn(expectedUsers);

        // Act
        ResponseEntity<List<UserResponse>> response = userController.findAll();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedUsers, response.getBody());
        verify(userService, times(1)).findAll();
    }

    @Test
    void findByRole_ShouldReturnListOfUsers_WhenAdmin() {
        // Arrange
        Role role = Role.CUSTOMER;
        UserResponse user = new UserResponse(1L, "customer@example.com",
                "Customer User", "+1234567890", Arrays.asList(role),
                true, true, "2026-01-01T00:00:00Z");
        List<UserResponse> expectedUsers = Arrays.asList(user);

        when(userService.findByRole(role)).thenReturn(expectedUsers);

        // Act
        ResponseEntity<List<UserResponse>> response = userController.findByRole(role);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedUsers, response.getBody());
        verify(userService, times(1)).findByRole(role);
    }

    @Test
    void update_ShouldReturnUpdatedUser_WhenValidRequest() {
        // Arrange
        Long userId = 1L;
        UserUpdateRequest request = new UserUpdateRequest("updated@example.com", "newpass123",
                "Updated User", "+1112223333", Arrays.asList(Role.CUSTOMER), true, true);
        UserResponse expectedResponse = new UserResponse(userId, "updated@example.com",
                "Updated User", "+1112223333", Arrays.asList(Role.CUSTOMER),
                true, true, "2026-01-01T00:00:00Z");

        when(userService.update(anyLong(), any(UserUpdateRequest.class))).thenReturn(expectedResponse);

        // Act
        ResponseEntity<UserResponse> response = userController.update(userId, request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(userService, times(1)).update(eq(userId), any(UserUpdateRequest.class));
    }

    @Test
    void delete_ShouldReturnNoContent_WhenAdmin() {
        // Arrange
        Long userId = 1L;
        doNothing().when(userService).delete(userId);

        // Act
        ResponseEntity<Void> response = userController.delete(userId);

        // Assert
        assertNotNull(response);
        assertEquals(204, response.getStatusCodeValue());
        verify(userService, times(1)).delete(userId);
    }

    @Test
    void findPendingApproval_ShouldReturnListOfUsers_WhenAdmin() {
        // Arrange
        UserResponse user = new UserResponse(1L, "pending@example.com",
                "Pending User", "+1234567890", Arrays.asList(Role.CUSTOMER),
                true, false, "2026-01-01T00:00:00Z");
        List<UserResponse> expectedUsers = Arrays.asList(user);

        when(userService.findPendingApproval()).thenReturn(expectedUsers);

        // Act
        ResponseEntity<List<UserResponse>> response = userController.findPendingApproval();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedUsers, response.getBody());
        verify(userService, times(1)).findPendingApproval();
    }

    @Test
    void approveUser_ShouldReturnApprovedUser_WhenAdmin() {
        // Arrange
        Long userId = 1L;
        UserResponse expectedResponse = new UserResponse(userId, "user@example.com",
                "Test User", "+1234567890", Arrays.asList(Role.CUSTOMER),
                true, true, "2026-01-01T00:00:00Z");

        when(userService.approveUser(userId)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<UserResponse> response = userController.approveUser(userId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(userService, times(1)).approveUser(userId);
    }

    @Test
    void revokeApproval_ShouldReturnUser_WhenAdmin() {
        // Arrange
        Long userId = 1L;
        UserResponse expectedResponse = new UserResponse(userId, "user@example.com",
                "Test User", "+1234567890", Arrays.asList(Role.CUSTOMER),
                true, false, "2026-01-01T00:00:00Z");

        when(userService.revokeApproval(userId)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<UserResponse> response = userController.revokeApproval(userId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
        verify(userService, times(1)).revokeApproval(userId);
    }
}