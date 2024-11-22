package faang.school.postservice.redis.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommentRedisDto implements Serializable {
    private Long id;
    private Long postId;
    private String content;
    private Long authorId;
    private LocalDateTime createdAt;
}