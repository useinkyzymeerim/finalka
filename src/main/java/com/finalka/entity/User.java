package com.finalka.entity;


import com.finalka.validations.ValidPassword;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;
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
        @NonNull
        @Column(name = "NAME",nullable = false)
        private String name;
        @NonNull
        @Column(name = "SURNAME",nullable = false)

        private String surname;
        @NonNull
        @Column(name = "USERNAME",nullable = false,length = 30,unique = true)
        private String username;
        @NotNull
        //@ValidPassword
        private String password;
        private String email;

        @ManyToMany(fetch = EAGER)
        private Set<Role> roles = new HashSet<>();

        private LocalDateTime lastAuthentication;

        private String resetToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Card> cards = new HashSet<>();
}

