package com.csye6225_ChenniXu.healthcheck.repository;

import com.csye6225_ChenniXu.healthcheck.model.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationRepository extends JpaRepository<EmailVerification, Long> {
    EmailVerification findByEmailAndUuid(String email, String uuid);
}
