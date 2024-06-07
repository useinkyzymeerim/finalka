package com.finalka.service.impl;


import com.finalka.dto.UpdateUserDto;
import com.finalka.dto.UserDto;
import com.finalka.entity.User;
import com.finalka.mapper.UserMapper;
import com.finalka.repo.UserRepo;
import com.finalka.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepo.findByUsername(username);
        User user = optionalUser.orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        user.setLastAuthentication(LocalDateTime.now());
        userRepo.save(user);

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public UserDto save(UserDto userDto) {
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User user = userMapper.toEntity(userDto);
        userRepo.save(user);
        sendRegistrationEmail(userDto);
        return userMapper.toDto(user);
    }

    private void sendRegistrationEmail(UserDto userDto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userDto.getEmail());
        message.setSubject("Регистрация успешна");
        message.setText("Регистрация прошла успешно! Добро пожаловать, " + userDto.getUsername() + "!");
        mailSender.send(message);
    }
    @Override
    public UserDto getById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь с таким id: " + id + " не найден"));
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        List<User> users = userRepo.findAll();
        return userMapper.toDtoList(users);
    }

    public UserDto updateUser(UpdateUserDto updateUserDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();

        if (currentUser == null) {
            log.error("Пользователь не найден по токену");
            throw new RuntimeException("Пользователь не найден по токену");
        }

        User user = userRepo.findByUsername(currentUser)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        user.setName(updateUserDto.getName());
        user.setSurname(updateUserDto.getSurname());
        user.setUsername(updateUserDto.getUsername());
        user.setEmail(updateUserDto.getEmail());

        userRepo.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public void generateResetToken(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден с помощью электронной почты: " + email));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepo.save(user);

        sendResetEmail(user.getEmail(), token);
    }

    private void sendResetEmail(String email, String token) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Запрос на сброс пароля");
        mailMessage.setText("Чтобы сбросить пароль, используйте следующий токен:\n" + token);
        mailSender.send(mailMessage);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        User user = userRepo.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Недействительный или просроченный token."));

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        user.setResetToken(null);
        userRepo.save(user);
    }
}
