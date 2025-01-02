package com.csye6225_ChenniXu.healthcheck.controller;

import com.csye6225_ChenniXu.healthcheck.model.EmailVerification;
import com.csye6225_ChenniXu.healthcheck.model.User;
import com.csye6225_ChenniXu.healthcheck.model.UserDTO;
import com.csye6225_ChenniXu.healthcheck.repository.VerificationRepository;
import com.csye6225_ChenniXu.healthcheck.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/verify")
public class VerificationController {

    @Autowired
    private VerificationRepository verificationRepository;
    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(VerificationController.class);

    @GetMapping
    public ResponseEntity<?> verifyUser(@RequestParam String email, @RequestParam String uuid) {
        try {
            uuid = uuid.replaceAll("\\.$", "");

            EmailVerification verification = verificationRepository.findByEmailAndUuid(email, uuid);

            if (verification != null) {
                if (verification.isVerified()) {
                    return new ResponseEntity<>("This link has already been used.", HttpStatus.FORBIDDEN);
                }
                if (verification.getExpirationTime().isBefore(LocalDateTime.now())) {
                    return new ResponseEntity<>("Verification link has expired.", HttpStatus.FORBIDDEN);
                }

                User user = new User();
                user.setEmail(verification.getEmail());
                user.setFirst_name(verification.getFirst_name());
                user.setLast_name(verification.getLast_name());
                user.setPassword(verification.getPassword());

                User createdUser = userService.createUser(user);
                UserDTO userDTO = new UserDTO(createdUser);

                verification.setVerified(true);
                verificationRepository.save(verification);

                return ResponseEntity.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("User verified.");
            } else {
                return new ResponseEntity<>("Invalid verification link.", HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error in get API", e);
            return new ResponseEntity<>("An error occurred during verification.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
