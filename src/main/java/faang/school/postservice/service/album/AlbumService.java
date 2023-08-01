package faang.school.postservice.service.album;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.exception.album.AlbumException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.album.AlbumValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final AlbumValidator albumValidator;
    private final UserContext userContext;


    public AlbumDto addPostToAlbum(long albumId, long postId) {
        long userId = userContext.getUserId();
        Album album = albumValidator.addPostToAlbumValidateService(userId, albumId, postId);
        AlbumDto albumDto = albumMapper.toDto(album);

        List<Long> postsIds = albumDto.getPostsIds();
        if (postsIds.contains(postId)) {
            throw new AlbumException("this post already exist in album");
        }
        postsIds.add(postId);

        albumDto.setPostsIds(postsIds);
        return albumMapper.toDto(albumRepository.save(albumMapper.toEntity(albumDto)));
    }

    public void deletePostFromAlbum(long albumId, long postIdToDelete) {
        long userId = userContext.getUserId();
        Album album = albumValidator.addPostToAlbumValidateService(userId, albumId, postIdToDelete);

        List<Post> posts = album.getPosts();
        List<Long> postsIds = posts.stream().map(Post::getId).toList();

        int id = postsIds.indexOf(postIdToDelete);

        if (id == -1) {
            throw new AlbumException("Undefined post");
        }

        posts.remove(id);
        album.setPosts(posts);

        albumRepository.save(album);
    }
}
