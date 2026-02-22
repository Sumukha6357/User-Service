package com.jsp.springboot.user_service.service;

import com.jsp.springboot.user_service.dto.UserCreateRequest;
import com.jsp.springboot.user_service.dto.UserPageResponse;
import com.jsp.springboot.user_service.dto.UserResponse;
import com.jsp.springboot.user_service.dto.UserUpdateRequest;

public interface UserService {

    UserResponse create(UserCreateRequest request);

    UserResponse getById(Long userId);

    UserPageResponse list(int page, int size, String sortBy, String direction, String search);

    UserResponse update(Long userId, UserUpdateRequest request);

    UserResponse softDelete(Long userId);
}
