package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumCreateUpdateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.service.album.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "API for managing posts' albums")
@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @Operation(description = "Create an empty album with given title and description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Album was created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid title or description provided"),
            @ApiResponse(responseCode = "404", description = "User with the provided ID not found in the database")
    })
    @PostMapping
    public ResponseEntity<AlbumDto> createAlbum(
            @RequestHeader("x-user-id")
            @Min(value = 1, message = "User ID must be greater than 0!")
            @Parameter(description = "ID of user who sent the request", required = true) String userId,
            @RequestBody @Valid AlbumCreateUpdateDto createDto
    ) {
        AlbumDto responseDto = albumService.createAlbum(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PatchMapping("/{albumId}/posts/{postId}")
    public ResponseEntity<AlbumDto> addPostToAlbum(
            @RequestHeader("x-user-id")
            @Min(value = 1, message = "User ID must be greater than 0!")
            @Parameter(description = "ID of user who sent the request") String userId,
            @PathVariable @Min(value = 1, message = "Album ID must be greater than 0!") long albumId,
            @PathVariable @Min(value = 1, message = "Post ID must be greater than 0!") long postId
    ) {
        AlbumDto responseDto = albumService.addPostToAlbum(albumId, postId);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{albumId}/posts/{postId}")
    public ResponseEntity<Void> deletePostFromAlbum(
            @RequestHeader("x-user-id")
            @Min(value = 1, message = "User ID must be greater than 0!")
            @Parameter(description = "ID of user who sent the request") String userId,
            @PathVariable @Min(value = 1, message = "Album ID must be greater than 0!") long albumId,
            @PathVariable @Min(value = 1, message = "Post ID must be greater than 0!") long postId
    ) {
        albumService.deletePostFromAlbum(albumId, postId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{albumId}/favorite")
    public ResponseEntity<Void> addAlbumToFavorites(
            @RequestHeader("x-user-id")
            @Min(value = 1, message = "User ID must be greater than 0!")
            @Parameter(description = "ID of user who sent the request") String userId,
            @PathVariable @Min(value = 1, message = "Album ID must be greater than 0!") long albumId
    ) {
        albumService.addAlbumToFavorites(albumId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{albumId}/favorite")
    public ResponseEntity<Void> deleteAlbumFromFavorites(
            @RequestHeader("x-user-id")
            @Min(value = 1, message = "User ID must be greater than 0!")
            @Parameter(description = "ID of user who sent the request") String userId,
            @PathVariable @Min(value = 1, message = "Album ID must be greater than 0!") long albumId
    ) {
        albumService.deleteAlbumFromFavorites(albumId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{albumId}")
    public ResponseEntity<AlbumDto> getAlbumById(
            @RequestHeader("x-user-id")
            @Min(value = 1, message = "User ID must be greater than 0!")
            @Parameter(description = "ID of user who sent the request") String userId,
            @PathVariable @Min(value = 1, message = "Album ID must be greater than 0!") long albumId
    ) {
        AlbumDto responseDto = albumService.getAlbumById(albumId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<AlbumDto>> getAllAlbums(
            @RequestHeader("x-user-id")
            @Min(value = 1, message = "User ID must be greater than 0!")
            @Parameter(description = "ID of user who sent the request") String userId,
            @RequestBody AlbumFilterDto filterDto
    ) {
        List<AlbumDto> filteredAlbums = albumService.getAllAlbums(filterDto);
        return ResponseEntity.ok(filteredAlbums);
    }

    @GetMapping("/user/filter")
    public ResponseEntity<List<AlbumDto>> getUserAlbums(
            @RequestHeader("x-user-id")
            @Min(value = 1, message = "User ID must be greater than 0!")
            @Parameter(description = "ID of user who sent the request") String userId,
            @RequestBody AlbumFilterDto filterDto
    ) {
        List<AlbumDto> filteredAlbums = albumService.getUserAlbums(filterDto);
        return ResponseEntity.ok(filteredAlbums);
    }

    @GetMapping("/user/favorite/filter")
    public ResponseEntity<List<AlbumDto>> getUserFavoriteAlbums(
            @RequestHeader("x-user-id")
            @Min(value = 1, message = "User ID must be greater than 0!")
            @Parameter(description = "ID of user who sent the request") String userId,
            @RequestBody AlbumFilterDto filterDto
    ) {
        List<AlbumDto> filteredAlbums = albumService.getUserFavoriteAlbums(filterDto);
        return ResponseEntity.ok(filteredAlbums);
    }

    @PatchMapping("/{albumId}")
    public ResponseEntity<AlbumDto> updateAlbum(
            @RequestBody @Valid AlbumCreateUpdateDto updateDto,
            @PathVariable @Min(1) long albumId
    ) {
        AlbumDto responseDto = albumService.updateAlbum(albumId, updateDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{albumId}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable @Min(1) long albumId) {
        albumService.deleteAlbum(albumId);
        return ResponseEntity.noContent().build();
    }
}
