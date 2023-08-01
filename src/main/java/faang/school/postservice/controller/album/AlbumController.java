package faang.school.postservice.controller.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.service.album.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/album")
public class AlbumController {
    private final AlbumService service;

    @PostMapping
    public AlbumDto addPostToAlbum(@RequestParam long albumId, @RequestParam long postId) {
        return service.addPostToAlbum(albumId, postId);
    }

    @DeleteMapping
    public void deletePostFromAlbum(@RequestParam long albumId, @RequestParam long postIdToDelete) {
         service.deletePostFromAlbum(albumId, postIdToDelete);
    }

}
