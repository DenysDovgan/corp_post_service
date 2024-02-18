package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.dto.ProjectDto;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostValidator postValidator;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostMapper postMapper;

    public PostDto createDraftPost(PostDto postDto) {
        UserDto author = null;
        ProjectDto project = null;

        if (postDto.getAuthorId() != null) {
            author = userServiceClient.getUser(postDto.getAuthorId());
        } else if (postDto.getProjectId() != null) {
            project = projectServiceClient.getProject(postDto.getProjectId());
        }
        postValidator.validateAuthorExists(author, project);

        return savePost(postDto);
    }

    private PostDto savePost(PostDto postDto) {
        Post post = postMapper.toEntity(postDto);
        return postMapper.toDto(postRepository.save(post));
    }

    public PostDto publishPost(long id) {
        Post post = findById(id);
        postValidator.validateIsNotPublished(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postMapper.toDto(postRepository.save(post));
    }

    public PostDto updatePost(UpdatePostDto postDto, long id) {
        Post post = findById(id);
        post.setContent(postDto.getContent());

        return postMapper.toDto(postRepository.save(post));
    }

    public void deletePost(long id) {
        Post post = findById(id);
        if (post.isDeleted()) {
            throw new DataValidationException("Пост уже удален");
        } else {
            post.setDeleted(true);
            postRepository.save(post);
        }
    }

    public PostDto getPost(long id) {
        Post post = findById(id);
        return postMapper.toDto(post);
    }

    public List<PostDto> getDraftsByUser(long userId) {
        List<Post> foundedPosts = postRepository.findByAuthorId(userId);
        return getSortedDrafts(foundedPosts);
    }

    public List<PostDto> getDraftsByProject(long projectId) {
        List<Post> foundedPosts = postRepository.findByProjectId(projectId);
        return getSortedDrafts(foundedPosts);
    }

    public List<PostDto> getPublishedPostsByUser(long userId) {
        List<Post> foundedPosts = postRepository.findByAuthorIdWithLikes(userId);
        return getSortedPublished(foundedPosts);
    }

    public List<PostDto> getPublishedPostsByProject(long projectId) {
        List<Post> foundedPosts = postRepository.findByProjectIdWithLikes(projectId);
        return getSortedPublished(foundedPosts);
    }

    private Post findById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пост с указанным ID не существует"));
    }

    private List<PostDto> getSortedDrafts(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted((post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt()))
                .map(postMapper::toDto)
                .toList();
    }

    private List<PostDto> getSortedPublished(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted((post1, post2) -> post2.getPublishedAt().compareTo(post1.getPublishedAt()))
                .map(postMapper::toDto)
                .toList();
    }
    @Transactional(readOnly = true)
    public Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
                new faang.school.postservice.exception.DataValidationException("Post has not found"));
    }
}
