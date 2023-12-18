package com.tpakhomova.tms.data;

import java.util.Objects;

public class User {
    private final Long id;
    private final String username;
    private final String passHash;
    private final String email;
    private final String firstName;
    private final String lastName;

    public User(Long id, String username, String passHash, String email, String firstName, String lastName) {
        this.id = id;
        this.username = username;
        this.passHash = passHash;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(username, that.username) && Objects.equals(passHash, that.passHash) && Objects.equals(email, that.email) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, passHash, email, firstName, lastName);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", passHash='" + passHash + '\'' + // todo remove
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
