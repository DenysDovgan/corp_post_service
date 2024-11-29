package faang.school.postservice.controller.resource;

import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resource")
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping("{postId}/addFiles")
    public List<ResourceDto> addFilesToPost(@PathVariable long postId, @RequestBody List<MultipartFile> files) {
        return resourceService.addFilesToPost(postId, files);
    }
}
