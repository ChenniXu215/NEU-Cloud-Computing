package com.csye6225_ChenniXu.healthcheck.model;

import jakarta.persistence.*;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "`user`")
public class User {
    @Id
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Email should not be empty")
    @Column(nullable = false, unique = true)
    private String email;


    @NotBlank(message = "Password should not be empty")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "First name should not be empty")
    @Column(nullable = false)
    private String first_name;

    @NotBlank(message = "Last name should not be empty")
    @Column(nullable = false)
    private String last_name;

    @Column(updatable = false)
    private String account_created;

    private String account_updated;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) { this.id = id;};

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getAccount_created() {
        return account_created;
    }

    public void setAccount_created(String account_created) {
        this.account_created = account_created;
    }

    public String getAccount_updated() {
        return account_updated;
    }

    public void setAccount_updated(String account_updated) {
        this.account_updated = account_updated;
    }
}
