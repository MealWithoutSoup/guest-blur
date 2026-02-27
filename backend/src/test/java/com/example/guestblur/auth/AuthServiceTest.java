package com.example.guestblur.auth;

import com.example.guestblur.user.UserEntity;
import com.example.guestblur.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void loadUserByUsernameReturnsUserDetails() {
        UserEntity user = new UserEntity("alice@example.com", "encoded", "Alice");
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        UserDetails result = authService.loadUserByUsername("alice@example.com");

        assertThat(result.getUsername()).isEqualTo("alice@example.com");
        assertThat(result.getPassword()).isEqualTo("encoded");
    }

    @Test
    void loadUserByUsernameThrowsWhenNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.loadUserByUsername("unknown@example.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void signupCreatesUserAndReturnsToken() {
        SignupRequest request = new SignupRequest("new@example.com", "password", "NewUser");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded-password");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken("new@example.com")).thenReturn("jwt-token");

        AuthResponse response = authService.signup(request);

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.email()).isEqualTo("new@example.com");
        assertThat(response.nickname()).isEqualTo("NewUser");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void signupThrowsWhenEmailAlreadyExists() {
        SignupRequest request = new SignupRequest("existing@example.com", "password", "User");
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already in use");
    }

    @Test
    void loginReturnsTokenForValidCredentials() {
        UserEntity user = new UserEntity("alice@example.com", "encoded", "Alice");
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded")).thenReturn(true);
        when(jwtService.generateToken("alice@example.com")).thenReturn("jwt-token");

        LoginRequest request = new LoginRequest("alice@example.com", "password");
        AuthResponse response = authService.login(request);

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.email()).isEqualTo("alice@example.com");
    }

    @Test
    void loginThrowsForInvalidEmail() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest("unknown@example.com", "password");

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void loginThrowsForInvalidPassword() {
        UserEntity user = new UserEntity("alice@example.com", "encoded", "Alice");
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        LoginRequest request = new LoginRequest("alice@example.com", "wrong");

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid credentials");
    }
}
