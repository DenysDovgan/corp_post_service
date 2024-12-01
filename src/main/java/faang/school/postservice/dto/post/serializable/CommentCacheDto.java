package faang.school.postservice.dto.post.serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static faang.school.postservice.utils.LocalDateTimePatterns.DATE_TIME_PATTERN;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentCacheDto {
    private Long id;
    private String content;
    private Long authorId;
    private Long postId;
    private UserDto authorDto;
    private Long likesCount;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime createdAt;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime updatedAt;
}
