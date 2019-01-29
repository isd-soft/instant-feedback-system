package com.inther.entities;

import javax.persistence.*;

@Entity
@Table(name = "user_authorities")
public class UserAuthorityEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authority_id")
    private Integer authorityId;

    @Column(name = "email")
    private String email;

    @Column(name = "authority")
    private String authority;

    public Integer getAuthorityId()
    {
        return authorityId;
    }

    public void setAuthorityId(Integer authorityId)
    {
        this.authorityId = authorityId;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getAuthority()
    {
        return authority;
    }

    public void setAuthority(String authority)
    {
        this.authority = authority;
    }

    @Override
    public String toString() {
        return "UserAuthorityEntity{" +
                "authorityId=" + authorityId +
                ", email='" + email + '\'' +
                ", authority='" + authority + '\'' +
                '}';
    }
}