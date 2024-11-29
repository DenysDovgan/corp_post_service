package faang.school.postservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResourceDto {
    private Long id;
    private String key;
    private long size;
    private LocalDateTime createdAt;
    private String name;
    private String type;
    private long postId;
}