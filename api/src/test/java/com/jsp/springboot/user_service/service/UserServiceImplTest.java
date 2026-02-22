package com.jsp.springboot.user_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.jsp.springboot.user_service.dto.UserCreateRequest;
import com.jsp.springboot.user_service.dto.UserResponse;
import com.jsp.springboot.user_service.entity.User;
import com.jsp.springboot.user_service.exception.ApiException;
import com.jsp.springboot.user_service.mapper.UserMapper;
import com.jsp.springboot.user_service.repository.UserRepository;
import com.jsp.springboot.user_service.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void create_shouldPersistUser() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUserName("alice");
        request.setAge(20);
        request.setGender("Female");

        User entity = new User();
        User saved = new User();
        saved.setUserId(1L);

        UserResponse response = new UserResponse();
        response.setUserId(1L);

        when(userRepository.existsByUserNameIgnoreCaseAndDeletedFalse("alice")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(saved);
        when(userMapper.toResponse(saved)).thenReturn(response);

        UserResponse result = userService.create(request);

        assertEquals(1L, result.getUserId());
        verify(userRepository).save(entity);
    }

    @Test
    void getById_shouldThrowWhenMissing() {
        when(userRepository.findByUserIdAndDeletedFalse(99L)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> userService.getById(99L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void softDelete_shouldMarkDeleted() {
        User user = new User();
        user.setUserId(10L);
        user.setDeleted(false);

        UserResponse response = new UserResponse();
        response.setUserId(10L);

        when(userRepository.findByUserIdAndDeletedFalse(10L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toResponse(any(User.class))).thenReturn(response);

        UserResponse result = userService.softDelete(10L);

        assertEquals(10L, result.getUserId());
        verify(userRepository).save(user);
    }
}
