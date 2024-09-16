package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class LikeController {
    private final LikeService service;

    @GetMapping("/getUsersLikedPost/{postId}")
    public List<UserDto> getUsersLikedPost(@PathVariable @Min(1L) long postId) {
        return service.getUsersLikedPost(postId);
    }

    @GetMapping("/getUsersLikedComm/{commentId}")
    public List<UserDto> getUsersLikedComm(@PathVariable @Min(1L) long commentId) {
        return service.getUsersLikedComm(commentId);
    }
}