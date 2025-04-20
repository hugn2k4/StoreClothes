package com.example.storeclothes.data.model;
import java.util.Date;
public class Admin extends User {

    // Constructor
    public Admin(String userId, String name, String email, String password, Date birthdate, Boolean isVerified, String role) {
        super(userId, name, email, password, birthdate, isVerified, role);
    }

    // Additional methods specific to Admin can be added here
}
