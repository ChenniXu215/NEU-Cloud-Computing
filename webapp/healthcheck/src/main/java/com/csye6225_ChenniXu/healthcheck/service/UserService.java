package com.csye6225_ChenniXu.healthcheck.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.time.format.DateTimeFormatter;

import com.csye6225_ChenniXu.healthcheck.repository.UserRepository;
import com.csye6225_ChenniXu.healthcheck.model.User;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MeterRegistry meterRegistry;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public User createUser(User user) throws Exception {
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

            if (existingUser.isPresent()) {
                throw new Exception("User already exists with email: " + user.getEmail());
            }
            if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
                throw new Exception("Invalid email format.");
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setAccount_created(LocalDateTime.now().format(formatter));
            user.setAccount_updated(LocalDateTime.now().format(formatter));

            return userRepository.save(user);
        } finally {
            sample.stop(Timer.builder("db.query.duration")
                    .description("Duration of createUser database query")
                    .tag("query", "createUser")
                    .register(meterRegistry));
        }
    }

    public void updateUser(UUID id, User user) throws Exception {
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            Optional<User> existingUser = userRepository.findById(id);
            if (!existingUser.isPresent()) {
                throw new Exception("User not found");
            }

            User userToUpdate = existingUser.get();

            // Ensure that only allowed fields are updated
            if (user.getId() != null) {
                throw new Exception("ID cannot be updated");
            }
            if (user.getEmail() != null) {
                throw new Exception("Email cannot be updated");
            }
            if (user.getAccount_created() != null) {
                throw new Exception("account_created cannot be updated");
            }
            if (user.getAccount_updated() != null) {
                throw new Exception("account_updated cannot be updated");
            }
            if (user.getFirst_name() != null && user.getFirst_name().length() == 0) {
                throw new Exception("Fist name cannot be empty");
            }
            if (user.getLast_name() != null && user.getLast_name().length() == 0) {
                throw new Exception("Last name cannot be empty");
            }
            if (user.getPassword() != null && user.getPassword().length() == 0) {
                throw new Exception("Password cannot be empty");
            }

            // Update first name, last name, and password if provided
            if (user.getFirst_name() != null && !user.getFirst_name().isEmpty()) {
                userToUpdate.setFirst_name(user.getFirst_name());
            }

            if (user.getLast_name() != null && !user.getLast_name().isEmpty()) {
                userToUpdate.setLast_name(user.getLast_name());
            }

            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                userToUpdate.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            // Automatically update the account_updated timestamp
            userToUpdate.setAccount_updated(LocalDateTime.now().format(formatter));

            // Save the updated user back to the repository
            userRepository.save(userToUpdate);
        } finally {
            sample.stop(Timer.builder("db.query.duration")
                    .description("Duration of updateUser database query")
                    .tag("query", "updateUser")
                    .register(meterRegistry));
        }

    }

    public Optional<User> getUserByEmail(String email) {
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            return userRepository.findByEmail(email);
        } finally {
            sample.stop(Timer.builder("db.query.duration")
                    .description("Duration of findByEmail database query")
                    .tag("query", "findByEmail")
                    .register(meterRegistry));
        }

    }
}

