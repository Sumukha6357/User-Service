package com.jsp.springboot.user_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserUpdateRequest {

    @NotBlank(message = "userName is required")
    @Size(min = 2, max = 120, message = "userName must be between 2 and 120 characters")
    private String userName;

    @Min(value = 1, message = "age must be at least 1")
    @Max(value = 120, message = "age must be at most 120")
    private int age;

    @NotBlank(message = "gender is required")
    @Pattern(regexp = "^(?i)(male|female|other)$", message = "gender must be Male, Female or Other")
    private String gender;

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}
