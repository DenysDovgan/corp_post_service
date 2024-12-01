package faang.school.postservice.service.postService;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.PostServiceValidator;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostServiceValidator validator;

    @Override
    public PostDto createDraft(PostDto postDto) {
        if(postDto.projectId() != null) {
            validator.validateProjectId(postDto.projectId());
        }
        if(postDto.authorId() != null) {
            validator.validateAuthorId(postDto.authorId());
        }

        Post savedPost = postRepository.save(postMapper.toEntity(postDto));
        return postMapper.toDto(savedPost);
    }

    @Override
    public PostDto publicPost(long id) {
        Post post = getPost(id);
        validator.checkPublicationPost(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        Post updatedPost = postRepository.save(post);

        return postMapper.toDto(updatedPost);
    }

    @Override
    public PostDto updatePost(PostDto postDto) {
        Post oldPost = null;
        if(postDto.id() != null) {
            oldPost = getPost(postDto.id());
            validator.validateAuthorsEquals(oldPost, postDto);

            if(!oldPost.getContent().equals(postDto.content())) {
                oldPost.setContent(postDto.content());
                oldPost.setUpdatedAt(LocalDateTime.now());
                return postMapper.toDto(postRepository.save(oldPost));
            }
        }

        return postMapper.toDto(oldPost);
    }

    @Override
    public PostDto softDeletePost(long id) {
        Post post = getPost(id);

        if(!post.isDeleted()) {
            post.setDeleted(true);
        }

        return postMapper.toDto(post);
    }

    @Override
    public PostDto getPostById(long id) {
        return postMapper.toDto(getPost(id));
    }

    @Override
    public List<PostDto> getPostDraftsByAuthorId(long id) {
        List<Post> allPosts = postRepository.findByAuthorId(id);

        return filerAndSortDraftPosts(allPosts);
    }

    @Override
    public List<PostDto> getPostDraftsByProjectId(long id) {
        List<Post> allPosts = postRepository.findByProjectId(id);

        return filerAndSortDraftPosts(allPosts);
    }

    @Override
    public List<PostDto> getPublishedPostsByAuthorId(long id) {
        List<Post> allPosts = postRepository.findByAuthorId(id);

        return filerAndSortPublishedPosts(allPosts);
    }

    @Override
    public List<PostDto> getPublishedPostsByProjectId(long id) {
        List<Post> allPosts = postRepository.findByProjectId(id);

        return filerAndSortPublishedPosts(allPosts);
    }

    private Post getPost(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Пост с id " + id + "не существует"));
    }

    private List<PostDto> filerAndSortDraftPosts(List<Post> posts) {
        return posts.stream()
                .filter(post -> Boolean.FALSE.equals(post.isPublished()) && Boolean.FALSE.equals(post.isDeleted()))
                .sorted(comparePostByCreateDateDesc)
                .map(postMapper::toDto)
                .toList();
    }

    private List<PostDto> filerAndSortPublishedPosts(List<Post> posts) {
        return posts.stream()
                .filter(post -> Boolean.TRUE.equals(post.isPublished()) && Boolean.FALSE.equals(post.isDeleted()))
                .sorted(comparePostByPublishDateDesc)
                .map(postMapper::toDto)
                .toList();
    }

    // правильно ли сделан компаратор и не нужно ли его вынести в отдельный файл куда-нибудь?
    Comparator<Post> comparePostByCreateDateDesc = (post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt());
    Comparator<Post> comparePostByPublishDateDesc = (post1, post2) -> post2.getPublishedAt().compareTo(post1.getPublishedAt());


}
