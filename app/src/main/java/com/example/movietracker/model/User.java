package com.example.movietracker.model;

public class User {
    private final int id;
    private final String fullName;
    private final String username;
    private final String email;

    public User(int id, String fullName, String username, String email) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
}
