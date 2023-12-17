package com.tpakhomova.tms.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String passHash;
    private String email;
    private String firstName;
    private String lastName;

    public User(Long id, String username, String passHash, String email, String firstName, String lastName) {
        this.id = id;
        this.username = username;
        this.passHash = passHash;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User() {}

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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassHash(String passHash) {
        this.passHash = passHash;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(passHash, user.passHash) && Objects.equals(email, user.email) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, passHash, email, firstName, lastName);
    }
}
