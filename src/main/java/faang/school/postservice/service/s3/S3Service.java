package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.postservice.exception.FileDownloadException;
import faang.school.postservice.exception.ResourceNotFoundException;
import faang.school.postservice.model.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public Resource uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s/%d/%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, file.getInputStream(), objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            log.error("Error occurred while uploading file {} to the cloud", file.getOriginalFilename(), e);
            throw new ResourceNotFoundException("No stream to upload file to the cloud");
        }
        Resource resource = new Resource();
        resource.setKey(key);
        resource.setSize(fileSize);
        resource.setCreatedAt(LocalDateTime.now());
        resource.setName(file.getOriginalFilename());
        resource.setType(file.getContentType());
        return resource;
    }

    public void deleteFile(String key) {
        s3Client.deleteObject(new DeleteObjectRequest(bucketName, key));
        log.info("Successfully deleted file {} with s3Client", key);
    }

    public InputStream downloadFile(String key) {
        try{
            S3Object s3Object = s3Client.getObject(bucketName, key);
            return s3Object.getObjectContent();
        }catch (Exception e){
            log.error("Can't get a stream to download file from the cloud", e);
            throw new FileDownloadException("Error occurred while downloading file with the key: " + key);
        }
    }
}