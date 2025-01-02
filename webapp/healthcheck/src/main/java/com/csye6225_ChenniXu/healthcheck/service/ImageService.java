package com.csye6225_ChenniXu.healthcheck.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.csye6225_ChenniXu.healthcheck.model.Image;
import com.csye6225_ChenniXu.healthcheck.repository.ImageRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.csye6225_ChenniXu.healthcheck.exception.UnauthorizedException;
import com.csye6225_ChenniXu.healthcheck.exception.ResourceNotFoundException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {

    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private ImageRepository imageRepository;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Autowired
    private MeterRegistry meterRegistry;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public Image uploadImage(MultipartFile file, UUID userId) throws IOException {
        Timer.Sample dbSample = Timer.start(meterRegistry);
        Timer.Sample s3Sample = Timer.start(meterRegistry);

        try {
            String uniqueFileName = "user-images/" + userId + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            try {
                amazonS3.putObject(bucketName, uniqueFileName, file.getInputStream(), metadata);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload file", e);
            } finally {
                s3Sample.stop(Timer.builder("aws.s3.duration")
                        .description("Duration of S3 putObject call")
                        .tag("operation", "createImage")
                        .register(meterRegistry));
            }

            String formattedDate = LocalDateTime.now().format(formatter);

            Image image = new Image();
            image.setFileName(file.getOriginalFilename());
            image.setUrl("https://" + bucketName + ".s3.amazonaws.com/" + uniqueFileName);
            image.setUploadDate(formattedDate);
            image.setUserId(userId);


            return imageRepository.save(image);
        } finally {
            dbSample.stop(Timer.builder("db.query.duration")
                    .description("Duration of createImage database query")
                    .tag("query", "createImage")
                    .register(meterRegistry));
        }
    }

    public void deleteImageByUserId(UUID userId) {
        Timer.Sample dbSample = Timer.start(meterRegistry);
        Timer.Sample s3Sample = Timer.start(meterRegistry);

        try {
            Image image = imageRepository.findByUserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Image not found for this user"));

            // Ensure user authorization before deletion
            if (!image.getUserId().equals(userId)) {
                throw new UnauthorizedException("You are not authorized to delete this image");
            }

            // Delete from S3
            String s3Key = image.getUrl().replace("https://" + bucketName + ".s3.amazonaws.com/", "");
            amazonS3.deleteObject(bucketName, s3Key);
            s3Sample.stop(Timer.builder("aws.s3.duration")
                    .description("Duration of S3 deleteObject call")
                    .tag("operation", "deleteImage")
                    .register(meterRegistry));

            // Delete from database
            imageRepository.delete(image);
        } finally {
            dbSample.stop(Timer.builder("db.query.duration")
                    .description("Duration of deleteImage database query")
                    .tag("query", "deleteImage")
                    .register(meterRegistry));
        }

    }

    public Optional<Image> getImageByUserId(UUID userId) {
        Timer.Sample dbSample = Timer.start(meterRegistry);

        try {
            Optional<Image> optionalImage = imageRepository.findByUserId(userId);
            return optionalImage;
        } finally {
            dbSample.stop(Timer.builder("db.query.duration")
                    .description("Duration of getImage database query")
                    .tag("query", "getImage")
                    .register(meterRegistry));
        }
    }

}
