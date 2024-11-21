package faang.school.postservice.service.impl.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.event.BanEvent;
import faang.school.postservice.model.event.CommentEvent;
import faang.school.postservice.mapper.comment.CommentMapperImpl;
import faang.school.postservice.model.entity.Comment;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.model.dto.comment.CommentRequestDto;
import faang.school.postservice.model.dto.comment.CommentResponseDto;
import faang.school.postservice.model.dto.user.UserDto;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.publisher.RedisBanMessagePublisher;
import faang.school.postservice.publisher.kafka.KafkaCommentProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.service.impl.comment.async.CommentServiceAsyncImpl;
import faang.school.postservice.validator.comment.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;
    @Spy
    private CommentMapperImpl commentMapper;
    @Mock
    private CommentValidator commentValidator;
    @Mock
    private CommentServiceAsyncImpl commentServiceAsync;
    @Mock
    private RedisBanMessagePublisher redisBanMessagePublisher;
    @Mock
    private CommentEventPublisher commentEventPublisher;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private KafkaCommentProducer kafkaCommentProducer;
    @Mock
    private RedisUserRepository redisUserRepository;


    @InjectMocks
    private CommentServiceImpl commentService;

    private Post post;
    private CommentRequestDto commentRequestDto;
    private CommentResponseDto commentResponseDto;
    private Comment comment;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .id(1L)
                .authorId(1L)
                .build();

        commentRequestDto = CommentRequestDto.builder()
                .id(1L)
                .content("This is a comment")
                .postId(1L)
                .build();

        commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .content("This is a comment")
                .postId(1L)
                .authorId(1L)
                .build();

        comment = Comment.builder()
                .id(1L)
                .content("This is a comment")
                .post(post)
                .authorId(1L)
                .verified(null)
                .verifiedDate(null)
                .build();

        ReflectionTestUtils.setField(commentService, "batchSize", 1);
    }

    @Test
    void create_whenUserAndPostExist_shouldCreateComment() {
        // given
        var user = UserDto.builder()
                .id(1L)
                .email("email@email.com")
                .username("username")
                .build();

        var commentEvent = CommentEvent.builder()
                .commentAuthorId(commentResponseDto.authorId())
                .username(user.getUsername())
                .postAuthorId(post.getAuthorId())
                .postId(post.getId())
                .content(commentResponseDto.content())
                .commentId(commentResponseDto.id())
                .build();

        doNothing().when(commentValidator).validateUser(anyLong());
        when(commentValidator.findPostById(anyLong())).thenReturn(post);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toResponseDto(any(Comment.class))).thenReturn(commentResponseDto);
        when(userServiceClient.getUser(anyLong())).thenReturn(user);
        // when
        CommentResponseDto result = commentService.create(1L, commentRequestDto);
        // then
        verify(commentValidator).validateUser(1L);
        verify(commentValidator).findPostById(1L);
        verify(commentRepository).save(comment);
        assertThat(result).isEqualTo(commentResponseDto);
        verify(commentEventPublisher).publish(commentEvent);
        verify(userServiceClient).getUser(1);
    }

    @Test
    void update_whenCommentExists_shouldUpdateComment() {
        // given
        when(commentValidator.findCommentById(anyLong())).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toResponseDto(any(Comment.class))).thenReturn(commentResponseDto);
        // when
        CommentResponseDto result = commentService.update(commentRequestDto);
        // then
        verify(commentValidator).findCommentById(commentRequestDto.id());
        verify(commentRepository).save(comment);
        assertThat(result).isEqualTo(commentResponseDto);
    }

    @Test
    void findAll_shouldReturnListOfComments() {
        // given
        List<Comment> comments = List.of(comment);
        when(commentRepository.findAllByPostId(anyLong())).thenReturn(comments);
        when(commentMapper.toResponseDto(anyList())).thenReturn(List.of(commentResponseDto));
        // when
        List<CommentResponseDto> result = commentService.findAll(1L);
        // then
        verify(commentRepository).findAllByPostId(1L);
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(commentResponseDto);
    }

    @Test
    void delete_whenCommentExists_shouldDeleteComment() {
        // when
        commentService.delete(1L);
        // then
        verify(commentRepository).deleteById(1L);
    }

    @Test
    void testCommentersBanCheck() {
        // Arrange
        Comment comment1 = new Comment();
        comment1.setAuthorId(1L);

        Comment comment2 = new Comment();
        comment2.setAuthorId(1L);

        Comment comment3 = new Comment();
        comment3.setAuthorId(2L);

        when(commentRepository.findAllByVerifiedFalse()).thenReturn(List.of(comment1, comment2, comment3));

        // Act
        commentService.commentersBanCheck(2);

        // Assert
        verify(redisBanMessagePublisher, times(1)).publish(new BanEvent(1L));
        verify(redisBanMessagePublisher, never()).publish(new BanEvent(2L));
    }

    @Test
    void testModerateComments() {
        Comment badComment = Comment.builder()
                .content("I have seen babushka")
                .verified(null)
                .verifiedDate(null)
                .build();
        List<Comment> comments = List.of(comment, badComment);

        when(commentRepository.findAllByVerifiedDateIsNull()).thenReturn(comments);

        commentService.moderateComments();

        verify(commentServiceAsync, times(2)).moderateCommentsByBatches(any());
    }
}