package faang.school.postservice.dto.album;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlbumRequestUpdateDto {

    private Long id;
    private String title;
    private String description;
    private Long authorId;
    private List<Long> postsIds;

}
