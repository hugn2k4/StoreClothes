package com.example.storeclothes.data.model;

import java.util.Date;

public class UserBuilder {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Date birthdate;
    private Boolean isVerified;
    private String role;
    private String address;
    private String phone;
    private String avatarUrl;
    private String status;

    public UserBuilder setUserId(String userId) {
        this.userId = userId;
        return this;
    }
    public UserBuilder setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }
    public UserBuilder setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
    public UserBuilder setEmail(String email) {
        this.email = email;
        return this;
    }
    public UserBuilder setPassword(String password) {
        this.password = password;
        return this;
    }
    public UserBuilder setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
        return this;
    }
    public UserBuilder setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
        return this;
    }
    public UserBuilder setRole(String role) {
        this.role = role;
        return this;
    }
    public UserBuilder setAddress(String address) {
        this.address = address;
        return this;
    }
    public UserBuilder setPhone(String phone) {
        this.phone = phone;
        return this;
    }
    public UserBuilder setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }
    public UserBuilder setStatus(String status) {
        this.status = status;
        return this;
    }
    public User build() {
        return new User(userId, firstName, lastName, email, password, birthdate, isVerified, role, address, phone, avatarUrl, status);
    }
}