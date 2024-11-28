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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final LikeMapper likeMapper;

    @Override
    public LikeDto createLikeComment(long id, LikeDto likeDto) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new DataValidationException("This comment does not exist in repository"));
        validateUserInRepository(likeDto.userId());

        Optional<Like> existingLikeOnComment = likeRepository.findByCommentIdAndUserId(comment.getId(), likeDto.userId());
        if (existingLikeOnComment.isPresent()) {
            throw new DataValidationException("User has already liked this comment");
        }
        Post post = comment.getPost();
        existingLikeOnPost(post, likeDto);

        Like like = likeMapper.toEntity(likeDto);
        like.setComment(comment);
        like.setPost(null);
        likeRepository.save(like);
        return likeMapper.toDto(like);
    }

    @Override
    public LikeDto createLikePost(long id, LikeDto likeDto) {
        Post post = postRepository.findById(id).orElseThrow(() -> new DataValidationException("This post does not exist in repository"));
        validateUserInRepository(likeDto.userId());
        List<Comment> comments = post.getComments().stream()
                .filter(comment1 -> comment1.getAuthorId() == likeDto.authorId())
                .toList();

        Optional<Like> existingLikeOnComment = comments.stream()
                .map(comment -> likeRepository.findByCommentIdAndUserId(comment.getId(), likeDto.userId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
        if (existingLikeOnComment.isPresent()) {
            throw new DataValidationException("User has already liked comment id " + existingLikeOnComment.get().getId());
        }
        existingLikeOnPost(post, likeDto);

        Like like = likeMapper.toEntity(likeDto);
        like.setPost(post);
        like.setComment(null);
        likeRepository.save(like);

        return likeMapper.toDto(like);
    }

    @Override
    public void deleteLikePost(Long id, Long userid) {
        Post post = postRepository.findById(id).orElseThrow(() -> new DataValidationException("This post does not exist in repository"));
        validateUserInRepository(userid);
        likeRepository.deleteByPostIdAndUserId(post.getId(), userid);
        log.info("Post ID {} has been deleted", id);
    }

    @Override
    public void deleteLikeComment(Long id, Long userid) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new DataValidationException("This comment does not exist in repository"));
        validateUserInRepository(userid);
        likeRepository.deleteByCommentIdAndUserId(comment.getId(), userid);
        log.info("Comment ID {} has been deleted", id);
    }

    private void validateUserInRepository(Long userid) {
        UserDto userDto = userServiceClient.getUser(userid);
        if (userDto == null) {
            throw new DataValidationException("User with ID " + userid + " not found");
        }
    }

    private void existingLikeOnPost(Post post, LikeDto likeDto) {
        Optional<Like> existingLikeOnPost = likeRepository.findByPostIdAndUserId(post.getId(), likeDto.userId());
        if (existingLikeOnPost.isPresent()) {
            throw new DataValidationException("User has already liked the post for this comment");
        }
    }
}