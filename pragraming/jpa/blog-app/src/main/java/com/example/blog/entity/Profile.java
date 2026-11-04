package com.example.blog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "profiles")
@Getter @Setter
public class Profile extends BaseEntity {

    @Column(length = 500)
    private String bio;

    @Column(length = 200)
    private String website;

    @Column(name = "avatar_url")
    private String avatarUrl;

    // سمت مالک OneToOne — FK اینجاست
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}