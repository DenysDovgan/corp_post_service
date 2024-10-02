package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    PostDto toDto(Post post);

    Post toEntity(PostDto postDto);

    List<PostDto> toDto(List<Post> post);

    List<Post> toEntity(List<PostDto> postDto);
}
