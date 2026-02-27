package com.example.guestblur.post;

import com.example.guestblur.auth.JwtService;
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
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String authToken;

    @BeforeEach
    void setUp() {
        UserEntity author = userRepository.save(
                new UserEntity("alice@test.com", passwordEncoder.encode("password"), "Alice Kim"));
        postRepository.save(new PostEntity("Spring Boot Tips", "This is the content of the post.", author));

        authToken = jwtService.generateToken("alice@test.com");
    }

    @Test
    void guestCanAccessPostList() throws Exception {
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Spring Boot Tips"))
                .andExpect(jsonPath("$[0].obfuscated").value(true));
    }

    @Test
    void guestGetPostListReturnsObfuscatedContent() throws Exception {
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(not("This is the content of the post.")))
                .andExpect(jsonPath("$[0].author").value(not("Alice Kim")))
                .andExpect(jsonPath("$[0].obfuscated").value(true));
    }

    @Test
    void authenticatedUserGetPostListReturnsOriginalContent() throws Exception {
        mockMvc.perform(get("/api/posts")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("This is the content of the post."))
                .andExpect(jsonPath("$[0].author").value("Alice Kim"))
                .andExpect(jsonPath("$[0].obfuscated").value(false));
    }

    @Test
    void guestCanAccessPostById() throws Exception {
        Long postId = postRepository.findAll().get(0).getId();

        mockMvc.perform(get("/api/posts/" + postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Spring Boot Tips"))
                .andExpect(jsonPath("$.obfuscated").value(true));
    }

    @Test
    void authenticatedUserGetPostByIdReturnsOriginal() throws Exception {
        Long postId = postRepository.findAll().get(0).getId();

        mockMvc.perform(get("/api/posts/" + postId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("This is the content of the post."))
                .andExpect(jsonPath("$.obfuscated").value(false));
    }

    @Test
    void guestCannotCreatePost() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New Post\",\"content\":\"Content\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void authenticatedUserCanCreatePost() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken)
                        .content("{\"title\":\"New Post\",\"content\":\"New Content\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Post"))
                .andExpect(jsonPath("$.obfuscated").value(false));
    }
}
