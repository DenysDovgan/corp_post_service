package faang.school.postservice.service.impl.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.event.BanEvent;
import faang.school.postservice.model.event.CommentEvent;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.entity.Comment;
import faang.school.postservice.model.dto.comment.CommentRequestDto;
import faang.school.postservice.model.dto.comment.CommentResponseDto;
import faang.school.postservice.model.event.newsfeed.CommentNewsFeedEvent;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.publisher.CommentNewsFeedProducer;
import faang.school.postservice.publisher.RedisBanMessagePublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.CommentServiceAsync;
import faang.school.postservice.validator.comment.CommentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CommentValidator commentValidator;
    private final RedisBanMessagePublisher redisBanMessagePublisher;
    private final CommentServiceAsync commentServiceAsync;
    private final CommentEventPublisher commentEventPublisher;
    private final CommentNewsFeedProducer commentNewsFeedProducer;

    @Value("${comments.batch-size}")
    private int batchSize;

    @Override
    @Transactional
    public CommentResponseDto create(long userId, CommentRequestDto dto) {
        commentValidator.validateUser(userId);
        var user = userServiceClient.getUser(userId);
        var post = commentValidator.findPostById(dto.postId());
        var comment = commentMapper.toEntity(dto);
        comment.setAuthorId(userId);
        comment.setPost(post);
        comment = commentRepository.save(comment);
        var event = CommentEvent.builder()
                .commentAuthorId(comment.getAuthorId())
                .username(user.username())
                .postAuthorId(post.getAuthorId())
                .postId(comment.getPost().getId())
                .content(comment.getContent())
                .commentId(comment.getId())
                .build();
        var commentNFEvent = CommentNewsFeedEvent.builder()
                .id(comment.getId())
                .authorId(comment.getAuthorId())
                .postId(comment.getPost().getId())
                .content(comment.getContent())
                .user(user)
                .build();
        commentEventPublisher.publish(event);
        commentNewsFeedProducer.produce(commentNFEvent);
        return commentMapper.toResponseDto(comment);
    }

    @Override
    @Transactional
    public CommentResponseDto update(CommentRequestDto dto) {
        var comment = commentValidator.findCommentById(dto.id());
        comment.setContent(dto.content());
        return commentMapper.toResponseDto(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDto> findAll(Long postId) {
        var comments = commentRepository.findAllByPostId(postId).stream()
                .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                .toList();
        return commentMapper.toResponseDto(comments);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public void commentersBanCheck(int unverifiedCommentsLimit) {
        Map<Long, Long> unverifiedAuthorsAndCommentsCount = commentRepository.findAllByVerifiedFalse().stream()
                .collect(Collectors.groupingBy(Comment::getAuthorId, Collectors.counting()));

        unverifiedAuthorsAndCommentsCount.entrySet().stream()
                .filter((longLongEntry -> longLongEntry.getValue() >= unverifiedCommentsLimit))
                .map((Map.Entry::getKey))
                .forEach((id) -> {
                    log.info("Publishing User ID to ban: {}", id);
                    redisBanMessagePublisher.publish(new BanEvent(id));
                });
    }

    @Override
    @Transactional
    public void moderateComments() {
        List<Comment> unverifiedPosts = commentRepository.findAllByVerifiedDateIsNull();
        List<List<Comment>> batches = ListUtils.partition(unverifiedPosts, batchSize);

        batches.forEach(commentServiceAsync::moderateCommentsByBatches);
    }
}