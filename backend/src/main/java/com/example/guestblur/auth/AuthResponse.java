package com.example.guestblur.auth;

public record AuthResponse(
        String token,
        String email,
        String nickname
) {
}
