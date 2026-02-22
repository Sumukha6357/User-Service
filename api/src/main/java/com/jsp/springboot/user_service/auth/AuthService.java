package com.jsp.springboot.user_service.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jsp.springboot.user_service.dto.LoginRequest;
import com.jsp.springboot.user_service.dto.LoginResponse;
import com.jsp.springboot.user_service.exception.ApiException;
import com.jsp.springboot.user_service.exception.ErrorCode;
import com.jsp.springboot.user_service.security.JwtService;

@Service
public class AuthService {

    private final SecurityUsersProperties props;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final String adminPasswordHash;
    private final String viewerPasswordHash;

    public AuthService(SecurityUsersProperties props, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.props = props;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.adminPasswordHash = passwordEncoder.encode(props.getAdminPassword());
        this.viewerPasswordHash = passwordEncoder.encode(props.getViewerPassword());
    }

    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername().trim();
        String role = null;
        String encodedPassword = null;

        if (username.equals(props.getAdminUsername())) {
            role = "ADMIN";
            encodedPassword = adminPasswordHash;
        } else if (username.equals(props.getViewerUsername())) {
            role = "VIEWER";
            encodedPassword = viewerPasswordHash;
        }

        if (role == null || !passwordEncoder.matches(request.getPassword(), encodedPassword)) {
            throw new ApiException(ErrorCode.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtService.generateToken(username, role);
        return new LoginResponse(token, "Bearer", jwtService.getExpirationSeconds(), role);
    }
}
