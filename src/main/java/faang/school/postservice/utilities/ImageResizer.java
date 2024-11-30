package faang.school.postservice.utilities;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;


@Component
public class ImageResizer {

    public MultipartFile resizeImage(MultipartFile image, int targetWidth, int targetHeight) throws IOException {

        BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
        if (bufferedImage == null) {
            throw new IllegalArgumentException("Invalid image file.");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(bufferedImage)
                .size(targetWidth, targetHeight)
                .outputFormat("jpg")
                .toOutputStream(outputStream);

        byte[] resizedImageBytes = outputStream.toByteArray();
        return new MultipartFile() {
            @Override
            public String getName() {
                return image.getName();
            }

            @Override
            public String getOriginalFilename() {
                return image.getOriginalFilename();
            }

            @Override
            public String getContentType() {
                return image.getContentType();
            }

            @Override
            public boolean isEmpty() {
                return resizedImageBytes.length == 0;
            }

            @Override
            public long getSize() {
                return resizedImageBytes.length;
            }

            @Override
            public byte[] getBytes() {
                return resizedImageBytes;
            }

            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream(resizedImageBytes);
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
                try (FileOutputStream fileOutputStream = new FileOutputStream(dest)) {
                    fileOutputStream.write(resizedImageBytes);
                }
            }
        };
    }
}
