package com.example.guestblur.post;

import com.example.guestblur.user.UserEntity;
import com.example.guestblur.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public List<PostEntity> findAll() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public PostEntity findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + id));
    }

    @Transactional
    public PostEntity create(String title, String content, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        PostEntity post = new PostEntity(title, content, user);
        return postRepository.save(post);
    }
}
