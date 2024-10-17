package com.example.musicapp;

public class User {
    private int id;
    private String username;
    private String password;
    private String phoneNumber;
    private String role; // New field for role

    // Constructor
    public User(int id, String username, String password, String phoneNumber, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public User(String username, String phoneNumber, String role) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }



    // Getters and Setters for all fields
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}

