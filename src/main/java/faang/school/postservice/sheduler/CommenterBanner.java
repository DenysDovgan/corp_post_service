package faang.school.postservice.sheduler;

import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommenterBanner {

    private final PostService postService;

    @Scheduled(cron = "${cron.expression.ban-comments}")
    public void banForComments() {
        postService.banUsersWithTooManyUnverifiedComments();
    }
}
