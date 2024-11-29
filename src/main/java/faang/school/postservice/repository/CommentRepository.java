package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    @Query("SELECT c FROM Comment c WHERE c.verified is null ")
    List<Comment> findAllUnCheckedComments();

    @Query(nativeQuery = true, value = """
                    SELECT author_id FROM comment
                    WHERE verified = FALSE
                    GROUP BY author_id
                    HAVING COUNT(*) > 5;
            """)
    List<Long> findAllWereVerifiedFalse();
}
