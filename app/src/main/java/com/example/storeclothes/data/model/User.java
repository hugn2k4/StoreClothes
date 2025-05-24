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

    public User() {}

    public User(String userId, String firstName, String lastName, String email, String password,
                Date birthdate, Boolean isVerified, String role, String address, String phone, String avatarUrl) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.birthdate = birthdate;
        this.isVerified = isVerified;
        this.role = role;
        this.address = address;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
    }

    // Getter
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

    // Setter
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
}