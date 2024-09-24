package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.enums.AuthorType;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostMapper postMapper;

    public PostService(PostRepository postRepository,
                       UserServiceClient userServiceClient,
                       ProjectServiceClient projectServiceClient,
                       PostMapper postMapper) {
        this.postRepository = postRepository;
        this.userServiceClient = userServiceClient;
        this.projectServiceClient = projectServiceClient;
        this.postMapper = postMapper;
    }

    public PostDto createPost(PostDto postDto) {
        if (postDto.getAuthorType() == AuthorType.USER) {
            UserDto user = userServiceClient.getUser(postDto.getAuthorId());
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }
        } else if (postDto.getAuthorType() == AuthorType.PROJECT) {
            ProjectDto project = projectServiceClient.getProject(postDto.getAuthorId());
            if (project == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
            }
        } else {
            throw new IllegalArgumentException("Invalid author type");
        }

        Post post = postMapper.toPost(postDto);
        Post savedPost = postRepository.save(post);

        return postMapper.toPostDto(savedPost);
    }

    public PostDto publishPost(Long id) {
        Post post = getPostById(id);

        if (post.isPublished()) {
            throw new IllegalStateException("Post is already published");
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);

        return postMapper.toPostDto(post);
    }

    public PostDto updatePost(Long id, PostDto postDto) {
        Post post = getPostById(id);

        if (!post.getAuthorId().equals(postDto.getAuthorId()) || !postDto.getAuthorType().equals(postDto.getAuthorType())) {
            throw new IllegalStateException("Cannot change author or author type of the post");
        }

        post.setContent(postDto.getContent());
        postRepository.save(post);

        return postMapper.toPostDto(post);
    }

    public void deletePost(Long id) {
        Post post = getPostById(id);
        post.setDeleted(true);
        postRepository.save(post);
    }

    public PostDto getPost(Long id) {
        Post post = getPostById(id);
        return postMapper.toPostDto(post);
    }

    public List<PostDto> getUserDrafts(Long authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .map(postMapper::toPostDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<PostDto> getProjectDrafts(Long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .map(postMapper::toPostDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<PostDto> getUserPublishedPosts(Long authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .map(postMapper::toPostDto)
                .sorted(Comparator.comparing(PostDto::getPublishedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<PostDto> getProjectPublishedPosts(Long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .map(postMapper::toPostDto)
                .sorted(Comparator.comparing(PostDto::getPublishedAt).reversed())
                .collect(Collectors.toList());
    }

    private Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found with id: " + id));
    }
}