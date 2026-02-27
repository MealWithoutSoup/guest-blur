package com.example.guestblur.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET = Base64.getEncoder()
            .encodeToString("test-secret-key-for-jwt-unit-testing-must-be-long-enough-256-bits".getBytes());
    private static final long EXPIRATION_MS = 86400000L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, EXPIRATION_MS);
    }

    @Test
    void generatesTokenForEmail() {
        String token = jwtService.generateToken("alice@example.com");

        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    void extractsEmailFromToken() {
        String email = "alice@example.com";
        String token = jwtService.generateToken(email);

        assertThat(jwtService.extractEmail(token)).isEqualTo(email);
    }

    @Test
    void validTokenReturnsTrue() {
        String token = jwtService.generateToken("alice@example.com");

        assertThat(jwtService.isValid(token)).isTrue();
    }

    @Test
    void invalidTokenReturnsFalse() {
        assertThat(jwtService.isValid("invalid.token.here")).isFalse();
    }

    @Test
    void expiredTokenReturnsFalse() {
        JwtService shortLivedService = new JwtService(SECRET, -1000L);
        String token = shortLivedService.generateToken("alice@example.com");

        assertThat(shortLivedService.isValid(token)).isFalse();
    }

    @Test
    void tamperedTokenReturnsFalse() {
        String token = jwtService.generateToken("alice@example.com");
        String tampered = token + "x";

        assertThat(jwtService.isValid(tampered)).isFalse();
    }

    @Test
    void differentEmailsProduceDifferentTokens() {
        String token1 = jwtService.generateToken("alice@example.com");
        String token2 = jwtService.generateToken("bob@example.com");

        assertThat(token1).isNotEqualTo(token2);
    }
}
