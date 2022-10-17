package com.example.intermediate.service;

import com.example.intermediate.domain.Post;
import com.example.intermediate.repository.CommentRepository;
import com.example.intermediate.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SchedulerService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    // 매일 1시에 실행
    @Scheduled(cron = "0 0 1 * * *")
    public void cronPostDelete() {
        for (Post post : postRepository.findAll()) {
            if (commentRepository.findAllByPost(post).isEmpty()) {
                log.info("게시물 <" + post.getTitle() + ">이 삭제되었습니다.");
                postRepository.deleteById(post.getId());
            }
        }
    }
}
