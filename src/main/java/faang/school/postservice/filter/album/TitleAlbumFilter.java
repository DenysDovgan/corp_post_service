package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class TitleAlbumFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto filter) {
        return filter.getTitlePattern() != null && !filter.getTitlePattern().isBlank();
    }

    @Override
    public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto filter) {
        return albums.filter(album -> album.getTitle().contains(filter.getTitlePattern()));
    }
}
