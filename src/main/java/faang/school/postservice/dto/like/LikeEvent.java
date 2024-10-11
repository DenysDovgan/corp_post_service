package faang.school.postservice.dto.like;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeEvent {
    private Long postId;
    private Long userId;
    private LocalDateTime createdAt;
}
