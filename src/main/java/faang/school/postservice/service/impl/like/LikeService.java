package faang.school.postservice.service.impl.like;

import faang.school.postservice.dto.LikeDto;

public interface LikeService {
    LikeDto createLikeComment(long id,LikeDto likeDto);
    LikeDto createLikePost(long id,LikeDto likeDto);
    void deleteLikePost(Long id,Long userid);
    void deleteLikeComment(Long id,Long userid);
}