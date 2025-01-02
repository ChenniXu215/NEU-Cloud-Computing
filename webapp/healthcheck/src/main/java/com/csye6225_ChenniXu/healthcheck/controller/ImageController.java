package com.csye6225_ChenniXu.healthcheck.controller;

import com.csye6225_ChenniXu.healthcheck.model.Image;
import com.csye6225_ChenniXu.healthcheck.model.User;
import com.csye6225_ChenniXu.healthcheck.repository.ImageRepository;
import com.csye6225_ChenniXu.healthcheck.repository.UserRepository;
import com.csye6225_ChenniXu.healthcheck.service.ImageService;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.csye6225_ChenniXu.healthcheck.exception.UnauthorizedException;
import com.csye6225_ChenniXu.healthcheck.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/v1/user/self")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private MeterRegistry meterRegistry;

    @Timed(value = "image.api.upload.call", description = "Time taken to upload an image")
    @PostMapping(value = "/pic", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile profilePic) {
        Counter counter = meterRegistry.counter("api.calls", "endpoint", "createImage");
        counter.increment();
        Timer.Sample sample = Timer.start(meterRegistry);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();

        Optional<User> currentUser = userRepository.findByEmail(currentEmail);

        if (!currentUser.isPresent()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "User not found");
            sample.stop(Timer.builder("api.duration")
                    .description("Duration of createImage API call")
                    .tag("endpoint", "createImage")
                    .register(meterRegistry));
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }

        try {
            Image uploadedImage = imageService.uploadImage(profilePic, currentUser.get().getId());
            if (currentUser.get().getId() == null) {
                throw new UnauthorizedException("Cannot find user id");
            }
            sample.stop(Timer.builder("api.duration")
                    .description("Duration of createImage API call")
                    .tag("endpoint", "createImage")
                    .register(meterRegistry));
            return ResponseEntity.status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(uploadedImage);
        } catch (IOException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            sample.stop(Timer.builder("api.duration")
                    .description("Duration of createImage API call")
                    .tag("endpoint", "createImage")
                    .register(meterRegistry));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            sample.stop(Timer.builder("api.duration")
                    .description("Duration of createImage API call")
                    .tag("endpoint", "createImage")
                    .register(meterRegistry));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }
    }

    @Timed(value = "image.api.get.call", description = "Time taken to get an image")
    @GetMapping(value = "/pic")
    public ResponseEntity<?> getProfileImage() {
        Counter counter = meterRegistry.counter("api.calls", "endpoint", "getImage");
        counter.increment();
        Timer.Sample sample = Timer.start(meterRegistry);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();

        Optional<User> currentUser = userRepository.findByEmail(currentEmail);

        if (!currentUser.isPresent()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "User not found");
            sample.stop(Timer.builder("api.duration")
                    .description("Duration of getImage API call")
                    .tag("endpoint", "getImageInfo")
                    .register(meterRegistry));
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }

        Optional<Image> image = imageService.getImageByUserId(currentUser.get().getId());
        if (image.isPresent()) {
            sample.stop(Timer.builder("api.duration")
                    .description("Duration of getImage API call")
                    .tag("endpoint", "getImageInfo")
                    .register(meterRegistry));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(image.get());
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Image not found");
            sample.stop(Timer.builder("api.duration")
                    .description("Duration of getImage API call")
                    .tag("endpoint", "getImageInfo")
                    .register(meterRegistry));
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }
    }

    @Timed(value = "image.api.delete.call", description = "Time taken to delete an image")
    @DeleteMapping(value = "/pic")
    public ResponseEntity<?> deleteProfileImage() {
        Counter counter = meterRegistry.counter("api.calls", "endpoint", "deleteImage");
        counter.increment();
        Timer.Sample sample = Timer.start(meterRegistry);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();

        Optional<User> currentUser = userRepository.findByEmail(currentEmail);

        if (!currentUser.isPresent()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "User not found");
            sample.stop(Timer.builder("api.duration")
                    .description("Duration of deleteImage API call")
                    .tag("endpoint", "deleteImageInfo")
                    .register(meterRegistry));
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }

        try {
            imageService.deleteImageByUserId(currentUser.get().getId());
            sample.stop(Timer.builder("api.duration")
                    .description("Duration of deleteImage API call")
                    .tag("endpoint", "deleteImageInfo")
                    .register(meterRegistry));
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            sample.stop(Timer.builder("api.duration")
                    .description("Duration of deleteImage API call")
                    .tag("endpoint", "deleteImageInfo")
                    .register(meterRegistry));
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        } catch (UnauthorizedException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            sample.stop(Timer.builder("api.duration")
                    .description("Duration of deleteImage API call")
                    .tag("endpoint", "deleteImageInfo")
                    .register(meterRegistry));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }
    }
}
