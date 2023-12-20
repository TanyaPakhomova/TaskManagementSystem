package com.tpakhomova.tms.persistence.data;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "username", unique = true)
    private String username;
    @Column(name = "pass_hash")
    private String passHash;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;

    @OneToMany(mappedBy="author")
    private List<TaskEntity> createdTasks;

    @OneToMany(mappedBy="assignee")
    private List<TaskEntity> assignedTasks;

    @OneToMany(mappedBy="author")
    private List<CommentEntity> comments;

    public UserEntity() {}

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

    public List<TaskEntity> getCreatedTasks() {
        return createdTasks;
    }

    public void setCreatedTasks(List<TaskEntity> createdTasks) {
        this.createdTasks = createdTasks;
    }

    public List<TaskEntity> getAssignedTasks() {
        return assignedTasks;
    }

    public void setAssignedTasks(List<TaskEntity> assignedTasks) {
        this.assignedTasks = assignedTasks;
    }

    public List<CommentEntity> getComments() {
        return comments;
    }

    public void setComments(List<CommentEntity> comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity userEntity = (UserEntity) o;
        return Objects.equals(id, userEntity.id) && Objects.equals(username, userEntity.username) && Objects.equals(passHash, userEntity.passHash) && Objects.equals(email, userEntity.email) && Objects.equals(firstName, userEntity.firstName) && Objects.equals(lastName, userEntity.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, passHash, email, firstName, lastName);
    }
}
