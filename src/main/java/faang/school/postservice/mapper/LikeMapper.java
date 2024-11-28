package faang.school.postservice.mapper;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {
    @Mapping(source = "idComment", target = "comment.id")
    @Mapping(source = "idPost", target = "post.id")
    Like toEntity(LikeDto likeDto);

    @Mapping(source = "comment.id", target ="idComment" )
    @Mapping(source = "post.id" , target = "idPost" )
    LikeDto toDto(Like like);
}