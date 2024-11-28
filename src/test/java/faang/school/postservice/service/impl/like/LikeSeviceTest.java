package faang.school.postservice.service.impl.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeSeviceTest {
    private LikeServiceImpl likeService;

    LikeRepository likeRepository = Mockito.mock(LikeRepository.class);
    CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    UserServiceClient userServiceClient = Mockito.mock(UserServiceClient.class);
    PostRepository postRepository = Mockito.mock(PostRepository.class);
    LikeMapper likeMapper = Mockito.mock(LikeMapper.class);

    long id;
    LikeDto likeDto;
    Comment comment;
    UserDto userDto;
    Like like;
    List<Like> likeList;
    Post post;
    Post post1;
    List<Comment> commentList;

    @BeforeEach
    void setUp() {
        likeService = new LikeServiceImpl(likeRepository, commentRepository, userServiceClient, postRepository, likeMapper);
        id = 1;
        likeDto = LikeDto.builder().userId(2L).authorId(6L).build();
        post = Post.builder().id(4L).build();
        comment = Comment.builder().post(post).id(1).build();
        userDto = new UserDto(5L);

        like = Like.builder().build();
        likeList = new ArrayList<>();
        likeList.add(like);

        commentList = new ArrayList<>();
        commentList.add(comment);
        post1 = Post.builder().id(4L).comments(commentList).build();
    }

    @Test
    public void testcreateLikeComment() {
        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));
        when(userServiceClient.getUser(likeDto.userId())).thenReturn(userDto);
        when(likeRepository.findByCommentIdAndUserId(comment.getId(), likeDto.userId())).thenReturn(Optional.empty());
        when(likeRepository.findByPostIdAndUserId(post.getId(), likeDto.userId())).thenReturn(Optional.empty());
        when(likeMapper.toEntity(likeDto)).thenReturn(like);
        when(likeRepository.save(like)).thenReturn(like);
        when(likeMapper.toDto(like)).thenReturn(likeDto);
        LikeDto result = likeService.createLikeComment(id, likeDto);
        assertEquals(result, likeDto);
    }

    @Test
    public void testvalidateUserInRepository() {
        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));
        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            likeService.createLikeComment(id, likeDto);
        });
        assertEquals("User with ID " + 2L + " not found", exception.getMessage());
    }

    @Test
    public void testcreateLikePost() {
        when(postRepository.findById(id)).thenReturn(Optional.of(post1));
        when(userServiceClient.getUser(likeDto.userId())).thenReturn(userDto);
        when(likeRepository.findByCommentIdAndUserId(comment.getId(), likeDto.userId())).thenReturn(Optional.of(like));
        when(likeRepository.findByPostIdAndUserId(post1.getId(), likeDto.userId())).thenReturn(Optional.empty());
        when(likeMapper.toEntity(likeDto)).thenReturn(like);
        when(likeRepository.save(like)).thenReturn(like);
        when(likeMapper.toDto(like)).thenReturn(likeDto);
        LikeDto result = likeService.createLikePost(id, likeDto);
        assertEquals(result, likeDto);
    }

    @Test
    public void testdeleteLikePost() {
        when(postRepository.findById(id)).thenReturn(Optional.of(post));
        when(userServiceClient.getUser(likeDto.userId())).thenReturn(userDto);
        likeRepository.deleteByPostIdAndUserId(post.getId(), likeDto.userId());
        verify(likeRepository, times(1)).deleteByPostIdAndUserId(post.getId(), likeDto.userId());
    }

    @Test
    public void testLikeComment() {
        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));
        when(userServiceClient.getUser(likeDto.userId())).thenReturn(userDto);
        likeRepository.deleteByCommentIdAndUserId(comment.getId(), likeDto.userId());
        verify(likeRepository, times(1)).deleteByCommentIdAndUserId(comment.getId(), likeDto.userId());
    }
}