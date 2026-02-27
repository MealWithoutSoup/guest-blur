package com.example.guestblur.config;

import com.example.guestblur.comment.CommentEntity;
import com.example.guestblur.comment.CommentRepository;
import com.example.guestblur.post.PostEntity;
import com.example.guestblur.post.PostRepository;
import com.example.guestblur.user.UserEntity;
import com.example.guestblur.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("!test")
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            UserRepository userRepository,
            PostRepository postRepository,
            CommentRepository commentRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }

            String encoded = passwordEncoder.encode("password");

            UserEntity alice = userRepository.save(new UserEntity("alice@example.com", encoded, "Alice Kim"));
            UserEntity bob = userRepository.save(new UserEntity("bob@example.com", encoded, "Bob Park"));
            UserEntity charlie = userRepository.save(new UserEntity("charlie@example.com", encoded, "Charlie Lee"));

            PostEntity post1 = postRepository.save(new PostEntity(
                    "Spring Boot에서 JWT 인증 구현하기",
                    "JWT(JSON Web Token)는 stateless 인증을 위한 표준입니다. Spring Security와 함께 사용하면 세션 없이도 안전한 인증을 구현할 수 있습니다. 이 글에서는 Access Token 발급부터 필터 체인 구성까지 단계별로 알아봅니다. 특히 토큰 만료 처리와 Refresh Token 전략에 대해서도 다룹니다.",
                    alice));

            PostEntity post2 = postRepository.save(new PostEntity(
                    "React에서 상태 관리 라이브러리 비교: Redux vs Zustand vs Jotai",
                    "프론트엔드 상태 관리는 항상 뜨거운 주제입니다. Redux는 안정적이지만 보일러플레이트가 많고, Zustand는 가볍고 직관적이며, Jotai는 원자적 상태 관리를 제공합니다. 프로젝트 규모와 팀 경험에 따라 적절한 선택이 달라집니다. 각각의 장단점을 실제 코드 예시와 함께 비교해봅니다.",
                    bob));

            PostEntity post3 = postRepository.save(new PostEntity(
                    "개발자가 알아야 할 Git 고급 명령어 10가지",
                    "git rebase, git cherry-pick, git bisect 등 일상적으로 자주 쓰이지는 않지만 알면 생산성이 크게 올라가는 Git 명령어들을 정리했습니다. 특히 git stash의 다양한 활용법과 git reflog로 실수를 복구하는 방법은 모든 개발자가 알아두면 좋습니다.",
                    charlie));

            // Comments for Post 1
            commentRepository.save(new CommentEntity(
                    "정말 유용한 글이네요! 저도 프로젝트에서 JWT를 적용하려고 했는데 이 글 덕분에 많이 도움이 됐습니다. 특히 Refresh Token 부분이 궁금했는데 잘 설명해주셨네요.",
                    post1, bob));
            commentRepository.save(new CommentEntity(
                    "Access Token의 만료 시간을 어느 정도로 설정하는 게 좋을까요? 보안과 사용자 경험 사이에서 고민이 됩니다. 보통 15분~1시간 정도가 적절하다고 하던데 실무에서는 어떻게 하시나요?",
                    post1, charlie));
            commentRepository.save(new CommentEntity(
                    "Spring Security 6에서 바뀐 부분도 있어서 업데이트된 내용도 추가해주시면 좋겠습니다. authorizeHttpRequests 문법이 좀 달라졌더라고요.",
                    post1, alice));

            // Comments for Post 2
            commentRepository.save(new CommentEntity(
                    "저는 개인 프로젝트에서 Zustand을 쓰고 있는데 정말 만족스럽습니다. Redux에 비해 코드량이 절반도 안 되는 것 같아요. 다만 대규모 프로젝트에서는 아직 Redux가 더 안정적이라는 의견도 있더라고요.",
                    post2, alice));
            commentRepository.save(new CommentEntity(
                    "Jotai는 아직 사용해본 적 없는데 Recoil이랑 비슷한 컨셉인가요? 원자적 상태 관리라는 개념이 흥미롭네요. 실제 프로덕션에서 쓰시는 분 계신가요?",
                    post2, charlie));
            commentRepository.save(new CommentEntity(
                    "서버 상태 관리는 TanStack Query로 분리하고, 클라이언트 상태만 Zustand으로 관리하는 패턴을 추천합니다. 이렇게 하면 상태 관리가 훨씬 깔끔해져요.",
                    post2, bob));
            commentRepository.save(new CommentEntity(
                    "Context API만으로도 충분한 경우가 많은데, 상태 관리 라이브러리를 도입하는 기준이 뭔가요? 성능 차이가 체감될 정도인지 궁금합니다.",
                    post2, alice));

            // Comments for Post 3
            commentRepository.save(new CommentEntity(
                    "git bisect는 정말 신세계였어요! 버그가 어느 커밋에서 생겼는지 이진 탐색으로 찾아주니까 디버깅 시간이 확 줄었습니다. 모든 개발자가 꼭 알아야 할 명령어라고 생각합니다.",
                    post3, alice));
            commentRepository.save(new CommentEntity(
                    "git reflog 덕분에 실수로 reset한 커밋을 살린 적이 있습니다. 그때의 감동이란... 정말 Git은 배울수록 강력한 도구라는 걸 느낍니다.",
                    post3, bob));
            commentRepository.save(new CommentEntity(
                    "git stash를 자주 쓰는데 stash list가 쌓이면 관리가 어려워지더라고요. stash에 이름 붙이는 팁(git stash push -m \"message\")이 정말 유용합니다!",
                    post3, charlie));
        };
    }
}
