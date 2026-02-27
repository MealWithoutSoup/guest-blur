package com.example.guestblur.comment;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getByPostId(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails user) {
        boolean isGuest = (user == null);
        List<CommentResponse> response = commentService.findByPostId(postId).stream()
                .map(comment -> CommentResponse.from(comment, isGuest))
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CommentResponse> create(
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal UserDetails user) {
        CommentEntity comment = commentService.create(postId, request.content(), user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommentResponse.from(comment, false));
    }
}
