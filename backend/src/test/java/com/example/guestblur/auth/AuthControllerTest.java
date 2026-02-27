package com.example.guestblur.auth;

import com.example.guestblur.user.UserEntity;
import com.example.guestblur.user.UserRepository;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.save(new UserEntity(
                "alice@test.com", passwordEncoder.encode("password"), "Alice"));
    }

    @Test
    void signupReturnsCreatedWithToken() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"new@test.com\",\"password\":\"password\",\"nickname\":\"NewUser\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("new@test.com"))
                .andExpect(jsonPath("$.nickname").value("NewUser"));
    }

    @Test
    void loginReturnsOkWithToken() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"alice@test.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("alice@test.com"));
    }

    @Test
    void signupWithInvalidEmailReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"not-an-email\",\"password\":\"password\",\"nickname\":\"User\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void signupWithShortPasswordReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@test.com\",\"password\":\"ab\",\"nickname\":\"User\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginWithWrongPasswordThrowsException() {
        assertThrows(ServletException.class, () ->
                mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"alice@test.com\",\"password\":\"wrong\"}")));
    }

    @Test
    void signupWithDuplicateEmailThrowsException() {
        assertThrows(ServletException.class, () ->
                mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"alice@test.com\",\"password\":\"password\",\"nickname\":\"User\"}")));
    }
}
