package faang.school.postservice.service.resource;

import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.exception.FileException;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final PostRepository postRepository;
    private final ResourceRepository resourceRepository;
    private final S3Service s3Service;
    private final ResourceMapper resourceMapper;

    public List<ResourceDto> addFilesToPost(long postId, List<MultipartFile> files) {
        if (files.size() > 10) {
            throw new IllegalStateException("Можно загрузить не более 10 изображений.");
        }

        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post не найден"));

        return files.stream()
                .peek(file -> {
                    if (file.getSize() > 5 * 1024 * 1024) {
                        throw new IllegalStateException("Размер файла не должен превышать 5 МБ");
                    }
                })
                .map(file -> CompletableFuture.supplyAsync(() -> fileUploadResult(file, post)))
                .map(CompletableFuture::join)
                .map(resourceMapper::toDto)
                .toList();

    }

    private Resource fileUploadResult(MultipartFile file, Post post) {
        String folder = "/post/images/".concat(String.valueOf(post.getId()));
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        InputStream inputStream = null;
        if (!file.isEmpty()) {
            try {

                BufferedImage image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
                if (image != null) {
                    int width = image.getWidth();
                    int height = image.getHeight();

                    if ((width > 1080 && height > 566) || (width > 1080 && height > 1080)) {
                        image = resizeImage(image, 1080, 566);
                        inputStream = convert(image, file.getContentType());
                    }
                }
            } catch (IOException e) {
                throw new FileException("Проблемы конвертации изображения", e);
            }
        }
        Resource result = s3Service.addResource(file, inputStream, key, post);
        return resourceRepository.save(result);
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, originalImage.getType());
        resizedImage.getGraphics().drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        return resizedImage;
    }

    private InputStream convert(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, format, os);
        return new ByteArrayInputStream(os.toByteArray());
    }
}
