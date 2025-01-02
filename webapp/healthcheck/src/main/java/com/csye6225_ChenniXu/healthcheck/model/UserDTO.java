package com.csye6225_ChenniXu.healthcheck.model;

import java.util.UUID;

public class UserDTO {
    private UUID id;
    private String email;
    private String first_name;
    private String last_name;
    private String account_created;
    private String account_updated;

    // Constructors, getters, and setters
    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.first_name = user.getFirst_name();
        this.last_name = user.getLast_name();
        this.account_created = user.getAccount_created();
        this.account_updated = user.getAccount_updated();
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getAccount_created() {
        return account_created;
    }

    public String getAccount_updated() {
        return account_updated;
    }

}