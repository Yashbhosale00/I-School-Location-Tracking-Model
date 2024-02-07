package com.example.locationdetect;

public class User {
    private String name, surname, email, password, username, gender, role, uid;

    // Required for calls to DataSnapshot.getValue(User.class)
    public User() {
    }

    // Constructor for creating a User object with userName and uid
    public User(String userName, String uid) {
        this.username = userName;  // Update to match the case sensitivity used in Firebase Database
        this.uid = uid;
    }

    // Constructor for creating a User object with all fields
    public User(String name, String surname, String email, String password, String username, String gender) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.username = username;  // Update to match the case sensitivity used in Firebase Database
        this.gender = gender;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
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

    public String getUsername() {
        return username;  // Update to match the case sensitivity used in Firebase Database
    }

    public void setUsername(String username) {
        this.username = username;  // Update to match the case sensitivity used in Firebase Database
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
