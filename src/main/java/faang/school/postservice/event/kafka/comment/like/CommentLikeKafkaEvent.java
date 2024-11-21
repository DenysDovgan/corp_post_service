package faang.school.postservice.event.kafka.comment.like;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.postservice.model.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentLikeKafkaEvent {

    @JsonProperty("comment_author_id")
    private Long commentAuthorId;
    @JsonProperty("like_author_id")
    private Long likeAuthorId;
    @JsonProperty("comment_id")
    private Long commentId;
    @JsonProperty("event_type")
    private EventType eventType;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
