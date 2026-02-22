package com.jsp.springboot.user_service.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.jsp.springboot.user_service.dto.UserCreateRequest;
import com.jsp.springboot.user_service.dto.UserResponse;
import com.jsp.springboot.user_service.dto.UserUpdateRequest;
import com.jsp.springboot.user_service.entity.User;

@Component
public class UserMapper {

    public User toEntity(UserCreateRequest request) {
        User user = new User();
        user.setUserName(request.getUserName().trim());
        user.setAge(request.getAge());
        user.setGender(normalizeGender(request.getGender()));
        return user;
    }

    public void applyUpdate(User user, UserUpdateRequest request) {
        user.setUserName(request.getUserName().trim());
        user.setAge(request.getAge());
        user.setGender(normalizeGender(request.getGender()));
    }

    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setUserName(user.getUserName());
        response.setAge(user.getAge());
        response.setGender(user.getGender());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }

    public List<UserResponse> toResponseList(List<User> users) {
        return users.stream().map(this::toResponse).toList();
    }

    private String normalizeGender(String gender) {
        String value = gender.trim().toLowerCase();
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }
}
