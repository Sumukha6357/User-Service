package com.jsp.springboot.user_service.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.jsp.springboot.user_service.auth.SecurityUsersProperties;

@Configuration
@EnableConfigurationProperties(SecurityUsersProperties.class)
public class PropertiesConfig {
}
