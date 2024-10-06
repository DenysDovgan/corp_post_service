package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.PostModerationException;
import faang.school.postservice.exception.PostRequirementsException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final UserContext userContext;
    private final ContentModerationService contentModerationService;
    private final ExecutorService postModerationThreadPool;

    @Value("${post.moderation.scheduler.batch.size}")
    private int batchSize;

    @Transactional
    public Post createDraftPost(Post post) {
        validateAuthorOrProject(post);
        markAsDraft(post);
        return postRepository.save(post);
    }

    @Transactional
    public Post publishPost(Long id) {
        Post existingPost = postRepository.findById(id).orElseThrow(() -> new PostRequirementsException("Post not found"));
        if (existingPost.isPublished()) {
            throw new PostRequirementsException("This post is already published");
        }
        publish(existingPost);
        return postRepository.save(existingPost);
    }

    @Transactional
    public Post updatePost(Long id, String content) {
        Post existingPost = postRepository.findById(id).orElseThrow(() -> new PostRequirementsException("Post not found"));
        updateContent(existingPost, content);
        return postRepository.save(existingPost);
    }

    @Transactional
    public Post deletePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostRequirementsException("Post not found"));
        delete(post);
        return postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public Post getPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new PostRequirementsException("Post not found"));
    }

    @Transactional(readOnly = true)
    public List<Post> getUserDrafts(long userId) {
        return postRepository.findDraftsByAuthorId(userId);
    }

    @Transactional(readOnly = true)
    public List<Post> getProjectDrafts(long projectId) {
        return postRepository.findDraftsByProjectId(projectId);
    }

    @Transactional(readOnly = true)
    public List<Post> getUserPublishedPosts(long userId) {
        return postRepository.findPublishedByAuthorId(userId);
    }

    @Transactional(readOnly = true)
    public List<Post> getProjectPublishedPosts(long projectId) {
        return postRepository.findPublishedByProjectId(projectId);
    }

    public void moderatePosts() {
        List<Post> unverifiedOrOldVerifiedPosts = postRepository.findUnverifiedOrOldVerifiedPosts(LocalDateTime.now().minusHours(24));
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < unverifiedOrOldVerifiedPosts.size(); i += batchSize) {
            int end = Math.min(i + batchSize, unverifiedOrOldVerifiedPosts.size());
            List<Post> batch = unverifiedOrOldVerifiedPosts.subList(i, end);

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (Post post : batch) {
                    try {
                        contentModerationService.checkContentAndModerate(post);
                    } catch (Exception e) {
                        log.error("Error moderating post with ID: {}. Error: {}", post.getId(), e.getMessage());
                        throw new PostModerationException("Error moderating post with ID: " + post.getId(), e);
                    }
                }
            }, postModerationThreadPool);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void validateAuthorOrProject(Post post) {
        if (Objects.nonNull(post.getAuthorId()) && Objects.nonNull(post.getProjectId())) {
            throw new DataValidationException("The post can't be made by both a user and a project at the same time.");
        }

        if (Objects.isNull(post.getAuthorId()) && Objects.isNull(post.getProjectId())) {
            throw new DataValidationException("The post must have either a user or a project as an author.");
        }

        if (Objects.nonNull(post.getAuthorId())) {
            validateUserExists(post);
        } else {
            validateProjectExists(post);
        }
    }

    private void validateUserExists(Post post) {
        userContext.setUserId(post.getAuthorId());
        userServiceClient.getUser(post.getAuthorId());
    }

    private void validateProjectExists(Post post) {
        projectServiceClient.getProject(post.getProjectId());
    }

    private void markAsDraft(Post post) {
        post.setPublished(false);
    }

    private void publish(Post post) {
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
    }

    private void updateContent(Post post, String content) {
        post.setContent(content);
        post.setUpdatedAt(LocalDateTime.now());
    }

    private void delete(Post post) {
        post.setDeleted(true);
        post.setPublished(false);
    }
}

