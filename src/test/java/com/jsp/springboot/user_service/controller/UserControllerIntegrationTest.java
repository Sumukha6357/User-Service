package com.jsp.springboot.user_service.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsp.springboot.user_service.entity.User;
import com.jsp.springboot.user_service.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        User user = new User();
        user.setUserName("seed-user");
        user.setAge(30);
        user.setGender("Male");
        userRepository.save(user);
    }

    @Test
    void viewer_shouldListUsers() throws Exception {
        String token = loginAndGetToken("viewer", "viewer123");

        mockMvc.perform(get("/api/v1/users")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.items").isArray());
    }

    @Test
    void viewer_shouldNotCreateUser() throws Exception {
        String token = loginAndGetToken("viewer", "viewer123");

        mockMvc.perform(post("/api/v1/users")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userName\":\"new-user\",\"age\":22,\"gender\":\"Male\"}"))
            .andExpect(status().isForbidden());
    }

    @Test
    void admin_shouldCreateUser() throws Exception {
        String token = loginAndGetToken("admin", "admin123");

        mockMvc.perform(post("/api/v1/users")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userName\":\"new-admin-user\",\"age\":22,\"gender\":\"Male\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.userId").exists());
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        String payload = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        return jsonNode.path("data").path("accessToken").asText();
    }
}
