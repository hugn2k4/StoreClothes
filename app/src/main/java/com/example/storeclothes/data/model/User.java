package com.example.storeclothes.data.model;

import java.util.Date;

public class User {
    private String userId;
    private String name;
    private String email;
    private String password;
    private Date birthdate;
    private Boolean isVerified;
    private String role; // USER hoặc ADMIN

    // Constructor
    public User(String userId, String name, String email, String password, Date birthdate, Boolean isVerified, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.birthdate = birthdate;
        this.isVerified = isVerified;
        this.role = role;
    }

    // Getter and Setter methods
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
