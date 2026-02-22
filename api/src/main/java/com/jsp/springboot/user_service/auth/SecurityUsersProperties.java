package com.jsp.springboot.user_service.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public class SecurityUsersProperties {

    private String jwtSecret;
    private long jwtExpirationSeconds = 3600;

    private String adminUsername;
    private String adminPassword;

    private String viewerUsername;
    private String viewerPassword;

    public String getJwtSecret() { return jwtSecret; }
    public void setJwtSecret(String jwtSecret) { this.jwtSecret = jwtSecret; }
    public long getJwtExpirationSeconds() { return jwtExpirationSeconds; }
    public void setJwtExpirationSeconds(long jwtExpirationSeconds) { this.jwtExpirationSeconds = jwtExpirationSeconds; }
    public String getAdminUsername() { return adminUsername; }
    public void setAdminUsername(String adminUsername) { this.adminUsername = adminUsername; }
    public String getAdminPassword() { return adminPassword; }
    public void setAdminPassword(String adminPassword) { this.adminPassword = adminPassword; }
    public String getViewerUsername() { return viewerUsername; }
    public void setViewerUsername(String viewerUsername) { this.viewerUsername = viewerUsername; }
    public String getViewerPassword() { return viewerPassword; }
    public void setViewerPassword(String viewerPassword) { this.viewerPassword = viewerPassword; }
}
