package faang.school.postservice.service.postService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostServiceValidator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    private final String POST_CONTENT = "postContent";
    private final long AUTHOR_ID = 1L;
    private final long PROJECT_ID = 2L;
    private final long POST_ID = 3L;

    @Mock
    private PostRepository postRepository;
    @Spy
    private PostMapperImpl postMapper;
    @Mock
    private PostServiceValidator validator;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    public void testCreateDraft() {
        PostDto postDto = new PostDto(null, POST_CONTENT, AUTHOR_ID, null, null, null,
                false, false, null);
        Post savedPost = Post.builder().id(POST_ID).content(POST_CONTENT).authorId(AUTHOR_ID).published(false)
                .deleted(false).likes(null).comments(null).build();
        PostDto savedPostDto = new PostDto(POST_ID, POST_CONTENT, AUTHOR_ID, null, List.of(), List.of(),
                false, false, null);
        Post entity = postMapper.toEntity(postDto);
        when(postRepository.save(entity)).thenReturn(savedPost);

        PostDto result = postService.createDraft(postDto);

        verify(postRepository, times(1)).save(entity);
        assertThat(result).isEqualTo(savedPostDto);
    }
}