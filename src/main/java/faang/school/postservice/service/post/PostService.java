package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.filter.FilterDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.filter.post.PostFilter;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.sort.PostField;
import faang.school.postservice.sort.SortBy;
import faang.school.postservice.validator.PostServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final List<PostFilter> postFilters;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final List<SortBy> sort;

    public PostDto createPost(PostDto postDto) {
        PostServiceValidator.checkDtoValidAuthorOrProjectId(postDto);
        PostServiceValidator.checkThatUserOrProjectIsExist(postDto, userServiceClient, projectServiceClient);


        Post post = postMapper.toEntity(postDto);
        post.setCreatedAt(LocalDateTime.now());
        post.setPublished(false);
        post.setDeleted(false);
        postRepository.save(post);



        return postMapper.toDto(post);
    }

    public PostDto publishPost(long id) {
        Post post = getPost(id);
        PostServiceValidator.checkPostWasPosted(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        post.setUpdatedAt(post.getPublishedAt());

        savePost(post);
        return postMapper.toDto(post);
    }

    public PostDto updatePost(long id, PostDto postDto) {
        Post post = getPost(id);

        postMapper.update(postDto, post);
        savePost(post);

        return postMapper.toDto(post);
    }

    public PostDto getPostDto(long id) {
        return postMapper.toDto(postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Не существует поста с таким id")));
    }

    public PostDto deletePost(long id) {
        Post post = getPost(id);

        post.setDeleted(true);
        post.setPublished(false);
        post.setUpdatedAt(LocalDateTime.now());

        savePost(post);
        return postMapper.toDto(post);
    }

    public List<PostDto> getPostsById(Long authorId, FilterDto filterDto) {
        List<Post> posts = getPostWithLikes(authorId, filterDto.getAuthor());

        if (posts == null) {
            return new ArrayList<>();
        }

        postFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .forEach(filter -> filter.apply(posts, filterDto));

        return sort(posts, filterDto.getPostField());
    }

    private List<PostDto> sort(List<Post> posts, PostField postField) {
        Comparator<Post> comparator = sort.stream()
                .filter(sortBy -> sortBy.getPostField()==postField)
                .findFirst()
                .map(SortBy::getComparator)
                .orElseThrow(()->new IllegalStateException("Такого компаратора не существует"));

        return posts.stream()
                .sorted(comparator)
                .map(postMapper::toDto)
                .toList();
    }
    private void addPostToProjectOrAuthor(){

    }

    private List<Post> getPostWithLikes(long id, boolean author) {
        if (author) {
            return postRepository.findByAuthorIdWithLikes(id);
        } else {
            return postRepository.findByProjectIdWithLikes(id);
        }
    }

    private Post getPost(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Не существует поста с таким id"));
    }

    private void savePost(Post post) {
        postRepository.save(post);
    }

}
