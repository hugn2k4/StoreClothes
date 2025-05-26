package com.example.storeclothes.data.model;

import java.util.Date;

public class User {
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

    // Firebase requires a no-argument constructor
    public User() {}

    // Constructor private - used only by Builder
    private User(Builder builder) {
        this.userId = builder.userId;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.password = builder.password;
        this.birthdate = builder.birthdate;
        this.isVerified = builder.isVerified;
        this.role = builder.role;
        this.address = builder.address;
        this.phone = builder.phone;
        this.avatarUrl = builder.avatarUrl;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Date getBirthdate() { return birthdate; }
    public Boolean getIsVerified() { return isVerified; }
    public String getRole() { return role; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getAvatarUrl() { return avatarUrl; }

    // Setters
    public void setUserId(String userId) { this.userId = userId; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setBirthdate(Date birthdate) { this.birthdate = birthdate; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
    public void setRole(String role) { this.role = role; }
    public void setAddress(String address) { this.address = address; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    // Builder pattern
    public static class Builder {
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

        public Builder() {}

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setBirthdate(Date birthdate) {
            this.birthdate = birthdate;
            return this;
        }

        public Builder setIsVerified(Boolean isVerified) {
            this.isVerified = isVerified;
            return this;
        }

        public Builder setRole(String role) {
            this.role = role;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public User build() {
            if (this.role == null) {
                this.role = "CUSTOMER";
            }
            if (this.isVerified == null) {
                this.isVerified = false;
            }
            if (this.birthdate == null) {
                this.birthdate = new Date();
            }
            return new User(this);
        }
    }
}
