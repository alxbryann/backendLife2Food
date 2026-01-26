package life2food.backend.service;

import io.awspring.cloud.s3.S3Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    public S3Service(S3Template s3Template) {
        this.s3Template = s3Template;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String key = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        s3Template.upload(bucketName, key, file.getInputStream());
        URL url = s3Template.download(bucketName, key).getURL();
        return url.toString();
    }
}
