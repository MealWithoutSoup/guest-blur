package com.example.guestblur.post;

import com.example.guestblur.presentation.TextObfuscator;

import java.time.Instant;

public record PostResponse(
        Long id,
        String title,
        String content,
        String author,
        Instant createdAt,
        boolean obfuscated
) {

    public static PostResponse from(PostEntity post, boolean obfuscate) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                obfuscate ? TextObfuscator.obfuscate(post.getContent()) : post.getContent(),
                obfuscate ? TextObfuscator.obfuscate(post.getAuthor().getNickname()) : post.getAuthor().getNickname(),
                post.getCreatedAt(),
                obfuscate
        );
    }
}
