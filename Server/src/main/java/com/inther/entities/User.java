package com.inther.entities;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@Entity
public class User
{
    @Id
    @Type(type="org.hibernate.type.PostgresUUIDType")
    @GeneratedValue
    private UUID id;

    private String email;
    private String password;
    private String role;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Presentation> presentations;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages;

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public User updateBy(User user) {
        this.email = user.email;
        this.password = user.password;
        this.role = user.role;
        return this;
    }
}