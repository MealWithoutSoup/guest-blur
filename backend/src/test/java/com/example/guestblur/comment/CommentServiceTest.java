package com.example.guestblur.comment;

import com.example.guestblur.post.PostEntity;
import com.example.guestblur.post.PostRepository;
import com.example.guestblur.user.UserEntity;
import com.example.guestblur.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void findByPostIdReturnsComments() {
        UserEntity author = new UserEntity("bob@example.com", "pw", "Bob");
        PostEntity post = new PostEntity("Title", "Content", author);
        CommentEntity comment = new CommentEntity("Great post!", post, author);
        when(commentRepository.findByPostIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(comment));

        List<CommentEntity> result = commentService.findByPostId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("Great post!");
    }

    @Test
    void findByPostIdReturnsEmptyListWhenNoComments() {
        when(commentRepository.findByPostIdOrderByCreatedAtDesc(1L)).thenReturn(List.of());

        List<CommentEntity> result = commentService.findByPostId(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void createSavesComment() {
        UserEntity author = new UserEntity("bob@example.com", "pw", "Bob");
        PostEntity post = new PostEntity("Title", "Content", author);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userRepository.findByEmail("bob@example.com")).thenReturn(Optional.of(author));
        when(commentRepository.save(any(CommentEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        CommentEntity result = commentService.create(1L, "Nice!", "bob@example.com");

        assertThat(result.getContent()).isEqualTo("Nice!");
        assertThat(result.getPost()).isEqualTo(post);
        assertThat(result.getAuthor()).isEqualTo(author);
        verify(commentRepository).save(any(CommentEntity.class));
    }

    @Test
    void createThrowsWhenPostNotFound() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.create(999L, "Comment", "bob@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Post not found");
    }

    @Test
    void createThrowsWhenUserNotFound() {
        UserEntity author = new UserEntity("bob@example.com", "pw", "Bob");
        PostEntity post = new PostEntity("Title", "Content", author);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.create(1L, "Comment", "unknown@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
    }
}
