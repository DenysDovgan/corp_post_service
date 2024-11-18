package faang.school.postservice.sort;

import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class SortByAlbumsSize extends SortBy {
    public SortByAlbumsSize() {
        super(PostField.ALBUMS);
    }

    @Override
    public Comparator<Post> getComparator() {
        return Comparator.comparing(post -> post.getAlbums().size());
    }
}