package faang.school.postservice.sheduler;

import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModerationScheduler {

    private final PostService postService;

    @Scheduled(cron = "${post.moderator.scheduler.cron}")
    public void moderationScheduler() {
        postService.moderatePosts();
    }
}