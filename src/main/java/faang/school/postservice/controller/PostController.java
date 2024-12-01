package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.PostControllerValidator;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostControllerValidator validator;
    private final PostService postService;

    @PostMapping
    public PostDto createDraftPost(@RequestBody @Valid PostDto postDto) {
        validator.validatePostCreators(postDto);
        return postService.createDraft(postDto);
    }

    @GetMapping("/{id}/public")
    public PostDto publicPost(@PathVariable long id) {
        validator.validateId(id);
        return postService.publicPost(id);
    }

    @PutMapping
    public PostDto updatePost(@RequestBody @Valid PostDto postDto) {
        validator.validatePostCreators(postDto);
        validator.validateId(postDto.id());
        return postService.updatePost(postDto);
    }

    @GetMapping("/{id}/delete")
    public PostDto softDeletePost(@PathVariable long id) {
        validator.validateId(id);
        return postService.softDeletePost(id);
    }

    @GetMapping("/{id}")
    public PostDto getPostById(@PathVariable long id) {
        return postService.getPostById(id);
    }

    @GetMapping("/author/{id}/drafts")
    public List<PostDto> getPostDraftsByAuthorId(@PathVariable long id) {
        validator.validateId(id);
        return postService.getPostDraftsByAuthorId(id);

    }

    @GetMapping("/project/{id}/drafts")
    public List<PostDto> getPostDraftsByProjectId(@PathVariable long id) {
        validator.validateId(id);
        return postService.getPostDraftsByProjectId(id);
    }

    @GetMapping("/author/{id}/published")
    public List<PostDto> getPublishedPostsByAuthorId(@PathVariable long id) {
        validator.validateId(id);
        return postService.getPublishedPostsByAuthorId(id);
    }

    @GetMapping("/project/{id}/published")
    public List<PostDto> getPublishedPostsByProjectId(@PathVariable long id) {
        validator.validateId(id);
        return postService.getPublishedPostsByProjectId(id);
    }
}
