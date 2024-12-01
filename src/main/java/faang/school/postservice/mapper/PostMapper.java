package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Post toEntity(PostDto postDto);

    @Mapping(source = "likes", target = "likesIds", qualifiedByName = "likesToDto")
    @Mapping(source = "comments", target = "commentsIds", qualifiedByName = "commentsToDto")
    PostDto toDto(Post post);

    @Named("likesToDto")
    default List<Long> likesToDto (List<Like> likes) {
        if(likes == null) {
            return List.of();
        }
        return likes.stream().map(Like::getId).toList();
    }

    @Named("commentsToDto")
    default List<Long> commentsToDto (List<Comment> comments) {
        if(comments == null) {
            return List.of();
        }
        return comments.stream().map(Comment::getId).toList();
    }
}
