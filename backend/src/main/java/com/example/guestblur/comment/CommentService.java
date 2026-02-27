package com.example.guestblur.comment;

import com.example.guestblur.post.PostEntity;
import com.example.guestblur.post.PostRepository;
import com.example.guestblur.user.UserEntity;
import com.example.guestblur.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public List<CommentEntity> findByPostId(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
    }

    @Transactional
    public CommentEntity create(Long postId, String content, String userEmail) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        CommentEntity comment = new CommentEntity(content, post, user);
        return commentRepository.save(comment);
    }
}
