package com.example.guestblur.post;

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
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void findAllReturnsPosts() {
        UserEntity author = new UserEntity("alice@example.com", "pw", "Alice");
        PostEntity post = new PostEntity("Title", "Content", author);
        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(post));

        List<PostEntity> result = postService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Title");
    }

    @Test
    void findByIdReturnsPost() {
        UserEntity author = new UserEntity("alice@example.com", "pw", "Alice");
        PostEntity post = new PostEntity("Title", "Content", author);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        PostEntity result = postService.findById(1L);

        assertThat(result.getTitle()).isEqualTo("Title");
    }

    @Test
    void findByIdThrowsWhenNotFound() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.findById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Post not found");
    }

    @Test
    void createSavesPost() {
        UserEntity author = new UserEntity("alice@example.com", "pw", "Alice");
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(author));
        when(postRepository.save(any(PostEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        PostEntity result = postService.create("Title", "Content", "alice@example.com");

        assertThat(result.getTitle()).isEqualTo("Title");
        assertThat(result.getContent()).isEqualTo("Content");
        assertThat(result.getAuthor()).isEqualTo(author);
        verify(postRepository).save(any(PostEntity.class));
    }

    @Test
    void createThrowsWhenUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.create("Title", "Content", "unknown@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
    }
}
