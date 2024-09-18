package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;
    private final UserContext userContext;

    @Transactional
    public PostDto createDraftPost(PostDto postDto) {
        Long userId = userContext.getUserId();
        postValidator.createDraftPostValidator(userId, postDto);

        Post post = postMapper.toEntity(postDto);

        post.setPublished(false);

        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public PostDto publishPost(PostDto postDto) {
        Post post = getPostFromRepository(postDto.id());

        postValidator.publishPostValidator(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public PostDto updatePost(PostDto postDto) {
        Post post = getPostFromRepository(postDto.id());

        postValidator.updatePostValidator(post, postDto);

        post.setTitle(postDto.title());
        post.setContent(postDto.content());

        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public PostDto softDeletePost(Long postId) {
        Post post = getPostFromRepository(postId);

        post.setPublished(false);
        post.setDeleted(true);

        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public PostDto getPost(Long id) {
        Post post = getPostFromRepository(id);

        return postMapper.toDto(post);
    }

    @Transactional
    public List<PostDto> getAllDraftsByAuthorId(Long userId) {
        postValidator.validateIfAuthorExists(userId);

        List<PostDto> posts = StreamSupport.stream(postRepository.findAll().spliterator(), false)
                .filter(post -> Objects.equals(post.getAuthorId(), userId))
                .filter(post -> !post.isPublished())
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();

        return posts;
    }

    @Transactional
    public List<PostDto> getAllDraftsByProjectId(Long projectId) {
        postValidator.validateIfProjectExists(projectId);

        List<PostDto> posts = StreamSupport.stream(postRepository.findAll().spliterator(), false)
                .filter(post -> Objects.equals(post.getProjectId(), projectId))
                .filter(post -> !post.isPublished())
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();

        return posts;
    }

    @Transactional
    public List<PostDto> getAllPublishedPostsByAuthorId(Long userId) {
        postValidator.validateIfAuthorExists(userId);

        List<PostDto> posts = StreamSupport.stream(postRepository.findAll().spliterator(), false)
                .filter(post -> Objects.equals(post.getAuthorId(), userId))
                .filter(Post::isPublished)
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toDto)
                .toList();

        return posts;
    }

    @Transactional
    public List<PostDto> getAllPublishedPostsByProjectId(Long projectId) {
        postValidator.validateIfProjectExists(projectId);

        List<PostDto> posts = StreamSupport.stream(postRepository.findAll().spliterator(), false)
                .filter(post -> Objects.equals(post.getProjectId(), projectId))
                .filter(Post::isPublished)
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();

        return posts;
    }

    private Post getPostFromRepository(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postId));
    }
}
