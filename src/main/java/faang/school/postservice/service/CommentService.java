package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentDto create(CommentDto commentDto, long postId) {
        validateAuthorExists(commentDto);

        Optional<Post> post = postRepository.findById(postId);
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(post.orElseThrow(() -> new IllegalArgumentException("Post ID is invalid")));
        return commentMapper.toDto(commentRepository.save(comment));
    }

    public CommentDto update(CommentDto commentDto, long postId) {
        Comment comment = validateCommentDto(commentDto, postId);
        comment.setContent(commentDto.getContent());
        return commentMapper.toDto(commentRepository.save(comment));
    }

    public CommentDto delete(CommentDto commentDto, long postId) {
        Comment comment = validateCommentDto(commentDto, postId);
        commentRepository.delete(comment);
        return commentDto;
    }

    public List<CommentDto> getAllCommentsByPostId(long postId) {
        List<Comment> commentList = commentRepository.findAllByPostId(postId);
        return commentList.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .map(commentMapper::toDto).toList();
    }

    private void validateAuthorExists(CommentDto commentDto) {
        UserDto userDto = userServiceClient.getUser(commentDto.getAuthorId());
        if (userDto == null || userDto.getId() == null) {
            throw new IllegalArgumentException("There are no author with id "+commentDto.getAuthorId());
        }
    }

    private Comment validateCommentDto(CommentDto commentDto, long id) {
        postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There are no post with id "+id));
        Comment comment = commentRepository.findById(commentDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("There are no comment with id "+commentDto.getId()));

        if (commentDto.getAuthorId() != comment.getAuthorId()) {
            throw new IllegalArgumentException("Only author can make changes! ID: "+commentDto.getAuthorId()+" is not valid");
        }

        if (id != comment.getPost().getId()) {
            throw new IllegalArgumentException("Post's ID is not invalid");
        }

        return comment;
    }
}
