package faang.school.postservice.dto.album;

import lombok.Data;

@Data
public class AlbumRequestDto {

    private Long id;
    private String title;
    private String description;
    private Long authorId;

}
