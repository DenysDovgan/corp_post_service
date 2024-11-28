package faang.school.postservice.redis.mapper;

import faang.school.postservice.model.dto.LikeDto;
import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.redis.model.dto.CommentRedisDto;
import faang.school.postservice.redis.model.entity.PostCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostCacheMapper {

    @Mapping(target = "comments", expression = "java(initializeComments())")
    @Mapping(target = "likes", expression = "java(initializeLikes())")
    PostCache toPostCache(PostDto postDto);

    default TreeSet<CommentRedisDto> initializeComments() {
        return new TreeSet<>();
    }

    default List<LikeDto> initializeLikes() {
        return new ArrayList<>();
    }
}
