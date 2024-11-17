package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public Post getPostById(long postId) {
        log.debug("found post by id {}", postId);
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post is not found"));
    }

    public boolean isPostNotExist(long postId){
        return !postRepository.existsById(postId);
    }
}
