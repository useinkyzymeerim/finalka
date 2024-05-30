package com.finalka.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.FetchType.EAGER;

    @Entity
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Table(name = "users")
    @Builder
    public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq_generator")
        @SequenceGenerator(name = "users_seq_generator", sequenceName = "users_seq", allocationSize = 1)
        private Long id;
        private String name;
        private String surname;
        private String username;
        private String password;
        private String email;

        @ManyToMany(fetch = EAGER)
        private Set<Role> roles = new HashSet<>();

        private LocalDateTime lastAuthentication;
        private String phoneNumber;


    }

