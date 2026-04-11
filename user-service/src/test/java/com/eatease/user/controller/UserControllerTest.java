package com.eatease.user.controller;

import com.eatease.common.constants.Role;
import com.eatease.user.dto.UserResponse;
import com.eatease.user.dto.UserUpdateRequest;
import com.eatease.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    @Test
    void getByIdShouldReturnUser() {

        UserResponse expected = new UserResponse(1L, "a@b.com", "A", "123", Set.of(Role.CUSTOMER), true, true, Instant.now());
        when(userService.getById(1L)).thenReturn(expected);

        ResponseEntity<UserResponse> response = controller.getById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }

    @Test
    void findAllShouldReturnUsers() {

        List<UserResponse> expected = List.of(
                new UserResponse(1L, "a@b.com", "A", "123", Set.of(Role.CUSTOMER), true, true, Instant.now())
        );
        when(userService.findAll()).thenReturn(expected);

        ResponseEntity<List<UserResponse>> response = controller.findAll();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }

    @Test
    void updateShouldReturnUpdatedUser() {

        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("Updated");
        UserResponse expected = new UserResponse(5L, "u@e.com", "Updated", "123", Set.of(Role.RESTAURANT_OWNER), true, false, Instant.now());
        when(userService.update(5L, request)).thenReturn(expected);

        ResponseEntity<UserResponse> response = controller.update(5L, request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }

    @Test
    void deleteShouldReturnNoContent() {


        ResponseEntity<Void> response = controller.delete(9L);

        assertEquals(204, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(userService).delete(9L);
    }
}
