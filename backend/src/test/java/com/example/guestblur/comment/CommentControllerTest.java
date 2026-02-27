package com.example.guestblur.comment;

import com.example.guestblur.auth.JwtService;
import com.example.guestblur.post.PostEntity;
import com.example.guestblur.post.PostRepository;
import com.example.guestblur.user.UserEntity;
import com.example.guestblur.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String authToken;
    private Long postId;

    @BeforeEach
    void setUp() {
        UserEntity author = userRepository.save(
                new UserEntity("bob@test.com", passwordEncoder.encode("password"), "Bob Park"));
        PostEntity post = postRepository.save(new PostEntity("Test Post", "Content", author));
        postId = post.getId();
        commentRepository.save(new CommentEntity("This is a comment with real content.", post, author));

        authToken = jwtService.generateToken("bob@test.com");
    }

    @Test
    void guestGetCommentsReturnsObfuscated() throws Exception {
        mockMvc.perform(get("/api/posts/" + postId + "/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(not("This is a comment with real content.")))
                .andExpect(jsonPath("$[0].author").value(not("Bob Park")))
                .andExpect(jsonPath("$[0].obfuscated").value(true));
    }

    @Test
    void authenticatedUserGetCommentsReturnsOriginal() throws Exception {
        mockMvc.perform(get("/api/posts/" + postId + "/comments")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("This is a comment with real content."))
                .andExpect(jsonPath("$[0].author").value("Bob Park"))
                .andExpect(jsonPath("$[0].obfuscated").value(false));
    }

    @Test
    void guestGetCommentsPreservesStructure() throws Exception {
        mockMvc.perform(get("/api/posts/" + postId + "/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].content").isString())
                .andExpect(jsonPath("$[0].author").isString())
                .andExpect(jsonPath("$[0].createdAt").isString())
                .andExpect(jsonPath("$[0].obfuscated").value(true));
    }

    @Test
    void guestCannotCreateComment() throws Exception {
        mockMvc.perform(post("/api/posts/" + postId + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"New comment\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void authenticatedUserCanCreateComment() throws Exception {
        mockMvc.perform(post("/api/posts/" + postId + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken)
                        .content("{\"content\":\"New comment\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("New comment"))
                .andExpect(jsonPath("$.obfuscated").value(false));
    }

    @Test
    void guestGetEmptyCommentListReturnsEmptyArray() throws Exception {
        UserEntity otherAuthor = userRepository.save(
                new UserEntity("other@test.com", passwordEncoder.encode("password"), "Other"));
        PostEntity emptyPost = postRepository.save(new PostEntity("Empty Post", "No comments here", otherAuthor));

        mockMvc.perform(get("/api/posts/" + emptyPost.getId() + "/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void authenticatedUserGetMultipleComments() throws Exception {
        UserEntity alice = userRepository.save(
                new UserEntity("alice@test.com", passwordEncoder.encode("password"), "Alice"));
        PostEntity post = postRepository.findById(postId).orElseThrow();
        commentRepository.save(new CommentEntity("Second comment", post, alice));

        mockMvc.perform(get("/api/posts/" + postId + "/comments")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
