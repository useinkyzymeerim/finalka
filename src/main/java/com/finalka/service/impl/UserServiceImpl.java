package com.finalka.service.impl;


import com.finalka.dto.UpdateUserDto;
import com.finalka.dto.UserDto;
import com.finalka.entity.User;
import com.finalka.exception.*;
import com.finalka.mapper.UserMapper;
import com.finalka.repo.UserRepo;
import com.finalka.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import lombok.extern.slf4j.Slf4j;
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
    public Long save(UserDto userDto) throws UsernameAlreadyExistsException, EmailSendingException, InvalidUserDataException {
        try {
            if (userRepo.existsByUsername(userDto.getUsername())) {
                throw new UsernameAlreadyExistsException("Имя пользователя уже существует");
            }

            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
            User user = userMapper.toEntity(userDto);
            user = userRepo.save(user);

            sendRegistrationEmail(userDto);

            return user.getId();
        } catch (DataIntegrityViolationException e) {
            throw new InvalidUserDataException("Ошибка целостности данных: " + e.getMessage());
        } catch (MailException e) {
            throw new EmailSendingException("Не удалось отправить письмо с подтверждением. Пожалуйста, попробуйте еще раз.", e);
        } catch (UsernameAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Не удалось сохранить пользователя", e);
        }
    }



    private void sendRegistrationEmail(UserDto userDto) throws EmailSendingException {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(userDto.getEmail());
            message.setSubject("Magic Menu");
            message.setText("Регистрация прошла успешно! Добро пожаловать, " + userDto.getUsername() + "!");
            mailSender.send(message);
        } catch (MailException e) {
            throw new EmailSendingException("Не удалось отправить письмо с подтверждением. Пожалуйста, попробуйте еще раз.", e);
        }
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

    public UserDto updateUser(UpdateUserDto updateUserDto) throws UnauthorizedException, InvalidUserDataException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getName() == null) {
                log.error("Пользователь не найден по токену");
                throw new UnauthorizedException("Пользователь не найден по токену");
            }

            String currentUser = authentication.getName();
            User user = userRepo.findByUsername(currentUser)
                    .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

            user.setName(updateUserDto.getName());
            user.setSurname(updateUserDto.getSurname());
            user.setUsername(updateUserDto.getUsername());
            user.setEmail(updateUserDto.getEmail());

            userRepo.save(user);
            return userMapper.toDto(user);
        } catch (UsernameNotFoundException e) {
            log.error("Пользователь не найден", e);
            throw new ResourceNotFoundException("Пользователь не найден", e);
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка целостности данных при обновлении пользователя", e);
            throw new InvalidUserDataException("Ошибка целостности данных при обновлении пользователя", e);
        } catch (UnauthorizedException e) {
            throw new UnauthorizedException(e.getMessage());
        }catch (Exception e) {
            log.error("Внутренняя ошибка сервера при обновлении пользователя", e);
            throw new RuntimeException("Внутренняя ошибка сервера при обновлении пользователя", e);
        }
    }


    @Override
    public void generateResetToken(String email) {
        try {
            User user = userRepo.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден с помощью электронной почты: " + email));

            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            userRepo.save(user);

            sendResetEmail(user.getEmail(), token);
        } catch (UsernameNotFoundException e) {
            log.error("Пользователь не найден: {}", email, e);
            throw new ResourceNotFoundException("Пользователь не найден с помощью электронной почты: " + email, e);
        } catch (MailException e) {
            log.error("Ошибка отправки почты на адрес: {}", email, e);
            throw new EmailSendingException("Ошибка отправки почты на адрес: " + email, e);
        } catch (RuntimeException e) {
            log.error("Ошибка генерации токена сброса для пользователя: {}", email, e);
            throw new RuntimeException("Ошибка генерации токена сброса для пользователя: " + email, e);
        }
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
        try {
            User user = userRepo.findByResetToken(token)
                    .orElseThrow(() -> new InvalidTokenException("Недействительный или просроченный token."));

            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);

            user.setResetToken(null);
            userRepo.save(user);
        } catch (InvalidTokenException e) {
            log.error("Ошибка при сбросе пароля: {}", e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.error("Внутренняя ошибка сервера при сбросе пароля", e);
            throw new RuntimeException("Внутренняя ошибка сервера при сбросе пароля", e);
        }
    }
}