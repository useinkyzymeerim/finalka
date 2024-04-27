package com.finalka.security;


import com.finalka.filter.CustomAuthenticationFilter;
import com.finalka.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@RequiredArgsConstructor
public class CustomSecurityDetails extends AbstractHttpConfigurer<CustomSecurityDetails, HttpSecurity> {

    private final UserRepo userRepo;

    @Override
    public void configure(HttpSecurity http) {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        CustomAuthenticationFilter filter = new CustomAuthenticationFilter(authenticationManager, userRepo);
        filter.setFilterProcessesUrl("/api/login");
        http.addFilter(filter);
    }

    public static CustomSecurityDetails customDsl(UserRepo userRepo) {
        return new CustomSecurityDetails(userRepo);
    }

}
