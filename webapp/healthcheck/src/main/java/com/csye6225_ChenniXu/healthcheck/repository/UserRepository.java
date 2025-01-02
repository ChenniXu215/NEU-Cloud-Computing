package com.csye6225_ChenniXu.healthcheck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.csye6225_ChenniXu.healthcheck.model.User;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}

