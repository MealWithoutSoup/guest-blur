package com.example.guestblur.auth;

import com.example.guestblur.user.UserEntity;
import com.example.guestblur.user.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return new User(user.getEmail(), user.getPassword(), List.of());
    }

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }
        String encoded = passwordEncoder.encode(request.password());
        UserEntity user = new UserEntity(request.email(), encoded, request.nickname());
        userRepository.save(user);
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getNickname());
    }

    public AuthResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getNickname());
    }
}
