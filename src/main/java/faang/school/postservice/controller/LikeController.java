package faang.school.postservice.controller;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.impl.like.LikeService;
import jakarta.transaction.Transactional;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/likes/comment/{id}")
    public LikeDto createLikeComment(@PathVariable("id") long id, @RequestBody LikeDto likeDto) {
        validateUserId(id, likeDto);
        return likeService.createLikeComment(id, likeDto);
    }

    @PostMapping("/likes/post/{id}")
    public LikeDto createLikePost(@PathVariable("id") long id, @RequestBody LikeDto likeDto) {
        validateUserId(id, likeDto);
        if (likeDto.authorId() == null) {
            throw new DataValidationException("ID user don't validate");
        }
        return likeService.createLikePost(id, likeDto);
    }

    @Transactional
    @DeleteMapping("/likes/post")
    public void deleteLikePost(@PathParam("id") Long id, @PathParam("userid") Long userid) {
        validateIdUseridNotNull(id, userid);
        likeService.deleteLikePost(id, userid);
    }

    @Transactional
    @DeleteMapping("/likes/comment")
    public void deleteLikeComment(@PathParam("id") Long id, @PathParam("userid") Long userid) {
        validateIdUseridNotNull(id, userid);
        likeService.deleteLikeComment(id, userid);
    }

    private void validateIdUseridNotNull(Long id, Long userid) {
        if (id == null || id <= 0 || userid == null || userid <= 0) {
            throw new DataValidationException("ID don't validate");
        }
    }

    private void validateUserId(long id, LikeDto likeDto) {
        if (id <= 0 || likeDto.userId() == null) {
            throw new DataValidationException("Don't validate");
        }
    }
}