package com.example.guestblur.comment;

import com.example.guestblur.presentation.TextObfuscator;

import java.time.Instant;

public record CommentResponse(
        Long id,
        String content,
        String author,
        Instant createdAt,
        boolean obfuscated
) {

    public static CommentResponse from(CommentEntity comment, boolean obfuscate) {
        return new CommentResponse(
                comment.getId(),
                obfuscate ? TextObfuscator.obfuscate(comment.getContent()) : comment.getContent(),
                obfuscate ? TextObfuscator.obfuscate(comment.getAuthor().getNickname()) : comment.getAuthor().getNickname(),
                comment.getCreatedAt(),
                obfuscate
        );
    }
}
