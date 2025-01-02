package com.csye6225_ChenniXu.healthcheck.repository;

import com.csye6225_ChenniXu.healthcheck.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {
    Optional<Image> findByUserId(UUID userId);
}

