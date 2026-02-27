package com.example.guestblur.post;

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
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAll(
            @AuthenticationPrincipal UserDetails user) {
        boolean isGuest = (user == null);
        List<PostResponse> response = postService.findAll().stream()
                .map(post -> PostResponse.from(post, isGuest))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        boolean isGuest = (user == null);
        PostEntity post = postService.findById(id);
        return ResponseEntity.ok(PostResponse.from(post, isGuest));
    }

    @PostMapping
    public ResponseEntity<PostResponse> create(
            @Valid @RequestBody CreatePostRequest request,
            @AuthenticationPrincipal UserDetails user) {
        PostEntity post = postService.create(request.title(), request.content(), user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PostResponse.from(post, false));
    }
}
