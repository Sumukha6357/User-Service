package com.jsp.springboot.user_service.dto;

public class LoginResponse {

    private String accessToken;
    private String tokenType;
    private long expiresInSeconds;
    private String role;

    public LoginResponse() {}

    public LoginResponse(String accessToken, String tokenType, long expiresInSeconds, String role) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresInSeconds = expiresInSeconds;
        this.role = role;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public long getExpiresInSeconds() { return expiresInSeconds; }
    public void setExpiresInSeconds(long expiresInSeconds) { this.expiresInSeconds = expiresInSeconds; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
