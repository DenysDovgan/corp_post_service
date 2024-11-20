package faang.school.postservice.service.impl;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostPublishedEvent;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Users;
import faang.school.postservice.publisher.KafkaPostProducer;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.UserRepository;
import faang.school.postservice.service.AsyncPostPublishService;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    @Value("${post.publisher.scheduler.size_batch}")
    private int sizeBatch;

    private final PostRepository postRepository;
    private final ProjectServiceClient projectServiceClient;
    private final UserServiceClient userServiceClient;
    private final PostValidator validator;
    private final PostMapper postMapper;
    private final AsyncPostPublishService asyncPostPublishService;
    private final KafkaPostProducer kafkaPostProducer;
    private final UserRepository userRepository;

    @Override
    public void createDraftPost(PostDto postDto) {
        validator.validatePost(postDto);
        if (existsCreator(postDto)) {
            throw new DataValidationException("There is no project/user");
        }

        postDto.setDeleted(false);
        postDto.setPublished(false);

        Post newPost = postMapper.toEntity(postDto);
        postRepository.save(newPost);
    }

    private boolean existsCreator(PostDto postDto) {
        if (postDto.getAuthorId() == null) {
            return projectServiceClient.existsProjectById(postDto.getProjectId());
        } else {
            return userServiceClient.existsUserById(postDto.getAuthorId());
        }
    }

    @Override
    @Transactional
    public void publishPost(long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no post with ID " + id));

        if (!post.isPublished()) {
            post.setPublishedAt(LocalDateTime.now());
            post.setPublished(true);
            postRepository.save(post);
            publishPostPublishedEvent(post);
        }
    }

    @Override
    public void updateContentPost(String newContent, long id) {
        postRepository.updateContentByPostId(id, newContent);
    }

    @Override
    public void softDeletePost(long id) {
        postRepository.softDeletePostById(id);
    }

    @Override
    public PostDto getPost(long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no post with ID " + id));
        return postMapper.toDto(post);
    }

    @Override
    public List<PostDto> getDraftPostsByUserId(long id) {
        List<Post> posts = postRepository.findByAuthorIdAndUnpublished(id);
        return postMapper.toDto(posts);
    }

    @Override
    public List<PostDto> getDraftPostsByProjectId(long id) {
        List<Post> posts = postRepository.findByProjectIdAndUnpublished(id);
        return postMapper.toDto(posts);
    }

    @Override
    public List<PostDto> getPublishedPostsByUserId(long id) {
        List<Post> posts = postRepository.findByAuthorIdAndPublished(id);
        return postMapper.toDto(posts);
    }

    @Override
    public List<PostDto> getPublishedPostsByProjectId(long id) {
        List<Post> posts = postRepository.findByProjectIdAndPublished(id);
        return postMapper.toDto(posts);
    }

    @Transactional
    @Override
    public void publishScheduledPosts() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        List<Post> postsToPublish = postRepository.findReadyToPublish();

        if (!postsToPublish.isEmpty()) {
            log.info("Size of posts list publish is {}", postsToPublish.size());
            List<List<Post>> subLists = ListUtils.partition(postsToPublish, sizeBatch);
            subLists.forEach(asyncPostPublishService::publishPost);
            log.info("Finished publish all posts at {}", currentDateTime);
        } else {
            log.info("Unpublished posts at {} not found", currentDateTime);
        }
    }

    @Override
    public List<Long> getAuthorsWithMoreFiveUnverifiedPosts() {
        return postRepository.findAuthorsWithMoreThanFiveUnverifiedPosts();
    }

    private void publishPostPublishedEvent(Post post) {
        Users user = userRepository.getReferenceById(post.getAuthorId());

        PostPublishedEvent event = new PostPublishedEvent();
        event.setPostId(post.getId());
        event.setAuthorId(post.getAuthorId());
        event.setSubscribersIds(user.getSubscribers().stream()
                .map(Users::getId)
                .toList());

        kafkaPostProducer.publish(event);
    }
}
