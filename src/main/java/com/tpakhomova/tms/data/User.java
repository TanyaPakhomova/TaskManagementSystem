package com.tpakhomova.tms.data;

public class User {
    private final String username;
    private final String passHash;
    private final String email;
    private final String firstName;
    private final String lastName;

    public User(String username, String passHash, String email, String firstName, String lastName) {
        this.username = username;
        this.passHash = passHash;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassHash() {
        return passHash;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
