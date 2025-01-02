package com.csye6225_ChenniXu.healthcheck.controller;

import com.csye6225_ChenniXu.healthcheck.model.EmailVerification;
import com.csye6225_ChenniXu.healthcheck.model.User;
import com.csye6225_ChenniXu.healthcheck.model.UserDTO;
import com.csye6225_ChenniXu.healthcheck.repository.UserRepository;
import com.csye6225_ChenniXu.healthcheck.repository.VerificationRepository;
import com.csye6225_ChenniXu.healthcheck.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/v1")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private VerificationRepository verificationRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");


    @Value("${sns.topic.arn}")
    private String topicArn;

    @Value("${aws.region:us-east-1}")
    private String awsRegion;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private AmazonSNS snsClient;

    @PostConstruct
    public void init() {
        this.snsClient = AmazonSNSClientBuilder.standard()
                .withRegion("us-east-1")
                .build();
    }

    @PostMapping(value = "/user", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        Counter counter = meterRegistry.counter("api.calls", "endpoint", "createUser");
        counter.increment();
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

            if (existingUser.isPresent()) {
                throw new Exception("User already exists with email: " + user.getEmail());
            }
            if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
                throw new Exception("Invalid email format.");
            }

            String verificationUUID = UUID.randomUUID().toString();
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(2);
            EmailVerification verification = new EmailVerification();
            verification.setEmail(user.getEmail());
            verification.setUuid(verificationUUID);
            verification.setPassword(user.getPassword());
            verification.setFirst_name(user.getFirst_name());
            verification.setLast_name(user.getLast_name());
            verification.setExpirationTime(expirationTime);
            verification.setVerified(false);
            verificationRepository.save(verification);

            Map<String, String> messageMap = new HashMap<>();
            messageMap.put("email", user.getEmail());
            messageMap.put("verification_uuid", verificationUUID);
            String message = new ObjectMapper().writeValueAsString(messageMap);

            PublishRequest publishRequest = new PublishRequest(topicArn, message);
            snsClient.publish(publishRequest);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Please verify your email. Verification email sent. Please check your inbox.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.FORBIDDEN);
    }

    @PutMapping(value ="/user/self", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        Counter counter = meterRegistry.counter("api.calls", "endpoint", "updateUser");
        counter.increment();
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            // Retrieve currently authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentEmail = authentication.getName(); // Email of the authenticated user
            // Fetch the current user based on email
            Optional<User> currentUser = userRepository.findByEmail(currentEmail);

            if (!currentUser.isPresent()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "User not found");
                sample.stop(Timer.builder("api.duration")
                        .description("Duration of updateUser API call")
                        .tag("endpoint", "updateUser")
                        .register(meterRegistry));
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorResponse);
            }

            userService.updateUser(currentUser.get().getId(), user);
            sample.stop(Timer.builder("api.duration")
                    .description("Duration of updateUser API call")
                    .tag("endpoint", "updateUser")
                    .register(meterRegistry));
            // Return 204 No Content on successful update
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            sample.stop(Timer.builder("api.duration")
                    .description("Duration of updateUser API call")
                    .tag("endpoint", "updateUser")
                    .register(meterRegistry));
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }
    }

    @GetMapping(value ="/user/self", produces = "application/json")
    public ResponseEntity<?> getUserInfo() {
        Counter counter = meterRegistry.counter("api.calls", "endpoint", "getUserInfo");
        counter.increment();
        Timer.Sample sample = Timer.start(meterRegistry);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();
        Optional<User> currentUser = userRepository.findByEmail(currentEmail);

        if (!currentUser.isPresent()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "User not found");
            sample.stop(Timer.builder("api.duration")
                    .description("Duration of getUserInfo API call")
                    .tag("endpoint", "getUserInfo")
                    .register(meterRegistry));
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }

        UserDTO userDTO = new UserDTO(currentUser.get());
        sample.stop(Timer.builder("api.duration")
                .description("Duration of getUserInfo API call")
                .tag("endpoint", "getUserInfo")
                .register(meterRegistry));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userDTO);
    }
}