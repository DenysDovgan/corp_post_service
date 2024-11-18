package faang.school.postservice.sort;

import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SortByCreatedAtTest {
    private SortByCreatedAt sortByCreatedAt = new SortByCreatedAt();

    @Test
    void testGetComparator() {
        Post post1 = new Post();
        Post post2 = new Post();
        Post post3 = new Post();

        post1.setCreatedAt(LocalDateTime.of(2024, 10, 14, 10, 10));
        post2.setCreatedAt(LocalDateTime.of(2024, 10, 11, 14, 10));
        post3.setCreatedAt(LocalDateTime.of(2024, 10, 10, 10, 10));

        List<Post> posts = Arrays.asList(post1, post2, post3);

        posts.sort(sortByCreatedAt.getComparator());

        assertEquals(post3, posts.get(0));
        assertEquals(post2, posts.get(1));
        assertEquals(post1, posts.get(2));
    }
}