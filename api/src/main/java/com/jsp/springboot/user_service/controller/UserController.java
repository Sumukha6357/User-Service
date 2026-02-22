package com.jsp.springboot.user_service.controller;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jsp.springboot.user_service.dto.ApiResponse;
import com.jsp.springboot.user_service.dto.UserCreateRequest;
import com.jsp.springboot.user_service.dto.UserPageResponse;
import com.jsp.springboot.user_service.dto.UserResponse;
import com.jsp.springboot.user_service.dto.UserUpdateRequest;
import com.jsp.springboot.user_service.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "${app.cors-origin:http://localhost:3000}")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody UserCreateRequest request) {
        UserResponse created = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>(HttpStatus.CREATED.value(), "User created", created));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','VIEWER')")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable Long userId) {
        UserResponse user = userService.getById(userId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "User fetched", user));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','VIEWER')")
    public ResponseEntity<ApiResponse<UserPageResponse>> list(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "userName") String sortBy,
        @RequestParam(defaultValue = "ASC") Sort.Direction direction,
        @RequestParam(required = false) String search
    ) {
        UserPageResponse users = userService.list(page, size, sortBy, direction.name(), search);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Users fetched", users));
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> update(
        @PathVariable Long userId,
        @Valid @RequestBody UserUpdateRequest request
    ) {
        UserResponse updated = userService.update(userId, request);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "User updated", updated));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> delete(@PathVariable Long userId) {
        UserResponse deleted = userService.softDelete(userId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "User soft-deleted", deleted));
    }
}
