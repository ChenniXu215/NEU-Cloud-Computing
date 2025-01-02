package com.csye6225_ChenniXu.healthcheck.model;

import jakarta.persistence.*;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "`image`")
public class Image {

    @Id
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(updatable = false, nullable = false)
    private String fileName;

    @Column(updatable = false, nullable = false)
    private String url;

    @Column(updatable = false, nullable = false)
    private String uploadDate;

    @Column(nullable = false)
    private UUID userId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
