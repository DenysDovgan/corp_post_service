package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.util.validator.comment.CommentServiceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentServiceValidator validator;
    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    private CommentDto commentDto;
    private Comment comment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        long commentId = 1L, authorId = 1L, postId = 1L;
        commentDto = CommentDto.builder().id(commentId).authorId(authorId).content("content").postId(postId).build();
        comment = Comment.builder().id(commentId).authorId(authorId).post(new Post()).content("content").build();
    }

    @Test
    public void testCreateComment() {
        commentService.createComment(commentDto);
        Mockito.verify(commentRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(commentMapper, Mockito.times(1)).toEntity(commentDto);
        Mockito.verify(commentMapper, Mockito.times(1)).toDto(Mockito.any());
    }
}