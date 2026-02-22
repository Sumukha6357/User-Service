package com.jsp.springboot.user_service.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jsp.springboot.user_service.dto.UserCreateRequest;
import com.jsp.springboot.user_service.dto.UserPageResponse;
import com.jsp.springboot.user_service.dto.UserResponse;
import com.jsp.springboot.user_service.dto.UserUpdateRequest;
import com.jsp.springboot.user_service.entity.User;
import com.jsp.springboot.user_service.exception.ApiException;
import com.jsp.springboot.user_service.exception.ErrorCode;
import com.jsp.springboot.user_service.mapper.UserMapper;
import com.jsp.springboot.user_service.repository.UserRepository;
import com.jsp.springboot.user_service.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final String USERNAME = "userName";
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse create(UserCreateRequest request) {
        if (userRepository.existsByUserNameIgnoreCaseAndDeletedFalse(request.getUserName().trim())) {
            throw new ApiException(ErrorCode.USERNAME_ALREADY_EXISTS, HttpStatus.CONFLICT, "User name already exists");
        }
        User user = userMapper.toEntity(request);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long userId) {
        return userMapper.toResponse(findActiveUser(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public UserPageResponse list(int page, int size, String sortBy, String direction, String search) {
        String sortColumn = mapSortColumn(sortBy);
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortColumn));

        Specification<User> spec = (root, query, cb) -> cb.isFalse(root.get("deleted"));
        if (search != null && !search.isBlank()) {
            String term = "%" + search.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get(USERNAME)), term));
        }

        Page<User> result = userRepository.findAll(spec, pageable);
        UserPageResponse response = new UserPageResponse();
        response.setItems(userMapper.toResponseList(result.getContent()));
        response.setPage(result.getNumber());
        response.setSize(result.getSize());
        response.setTotalElements(result.getTotalElements());
        response.setTotalPages(result.getTotalPages());
        response.setHasNext(result.hasNext());
        return response;
    }

    @Override
    public UserResponse update(Long userId, UserUpdateRequest request) {
        User user = findActiveUser(userId);
        if (userRepository.existsByUserNameIgnoreCaseAndUserIdNotAndDeletedFalse(request.getUserName().trim(), userId)) {
            throw new ApiException(ErrorCode.USERNAME_ALREADY_EXISTS, HttpStatus.CONFLICT, "User name already exists");
        }
        userMapper.applyUpdate(user, request);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse softDelete(Long userId) {
        User user = findActiveUser(userId);
        user.setDeleted(true);
        return userMapper.toResponse(userRepository.save(user));
    }

    private User findActiveUser(Long userId) {
        return userRepository.findByUserIdAndDeletedFalse(userId)
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND, "User not found"));
    }

    private String mapSortColumn(String sortBy) {
        if ("age".equalsIgnoreCase(sortBy)) {
            return "age";
        }
        if ("createdAt".equalsIgnoreCase(sortBy)) {
            return "createdAt";
        }
        return USERNAME;
    }
}
