package com.example.guestblur.comment;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentRequest(
        @NotBlank String content
) {
}
