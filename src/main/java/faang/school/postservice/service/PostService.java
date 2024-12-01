package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import java.util.List;

public interface PostService {
    PostDto createDraft(PostDto postDto);

    PostDto publicPost(long id);

    PostDto updatePost(PostDto postDto);

    PostDto softDeletePost(long id);

    PostDto getPostById(long id);

    List<PostDto> getPostDraftsByAuthorId(long id);

    List<PostDto> getPostDraftsByProjectId(long id);

    List<PostDto> getPublishedPostsByAuthorId(long id);

    List<PostDto> getPublishedPostsByProjectId(long id);
}
