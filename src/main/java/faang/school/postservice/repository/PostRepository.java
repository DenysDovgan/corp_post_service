package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @LazyCollection(LazyCollectionOption.TRUE)
    @Query("SELECT p FROM Post p WHERE p.published = false "
            + "AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query("SELECT p FROM Post p WHERE p.verifiedDate IS NULL")
    List<Post> findReadyToVerified();

    @Query(nativeQuery = true, value = """
        SELECT author_id 
        FROM post p
        WHERE p.verified = false  AND p.verified_date IS NOT NULL AND p.verified_date >= :fromDate
        GROUP BY p.author_id
        HAVING COUNT(*) >= :limit
        """)
    List<Long> findAuthorsWithUnverifiedPosts(int limit, LocalDate fromDate);

//    @Query(value = "SELECT p FROM post p WHERE p.authorId = :authorId ORDER BY p.timestamp DESC")
//    List<Post> findLimitedPostsByAuthorId(@Param("authorId") long authorId, Pageable pageable);
}
