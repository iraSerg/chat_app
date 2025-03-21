package com.irina.chat_app.service;

import com.irina.chat_app.dto.UserCreateDto;
import com.irina.chat_app.dto.UserEditDto;
import com.irina.chat_app.dto.UserReadDto;
import com.irina.chat_app.entity.Role;
import com.irina.chat_app.entity.User;
import com.irina.chat_app.exception.UserNotFoundException;
import com.irina.chat_app.mapper.UserCreateMapper;
import com.irina.chat_app.mapper.UserEditMapper;
import com.irina.chat_app.mapper.UserReadMapper;
import com.irina.chat_app.repository.RoleRepository;
import com.irina.chat_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final ImageUserService imageService;
    private final UserReadMapper userReadMapper;
    private final UserCreateMapper userCreateMapper;
    private final UserEditMapper userEditMapper;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public UserReadDto create(UserCreateDto userDto) {
        log.info("Creating user with username: {}", userDto.getUsername());
        return Optional.of(userDto)
                .map(userCreateMapper::map)
                .map(user -> {
                    Role userRole = roleRepository.findByName("ROLE_USER");
                    user.addRoles(Set.of(userRole));
                    return user;
                })
                .map(userRepository::save)
                .map(userReadMapper::map)
                .orElseThrow(() -> new IllegalStateException("Failed to create user"));
    }

    @Transactional
    public UserReadDto update(String username, UserEditDto userDto) {
        log.info("Updating user with username: {}", username);
        return userRepository.findByUsername(username)
                .map(entity -> {
                    uploadImage(userDto.getImage());
                    return userEditMapper.map(userDto, entity);
                })
                .map(userRepository::saveAndFlush)
                .map(userReadMapper::map).orElseThrow(() -> new UserNotFoundException("User  with username " + username + " not found"));
    }

    @Transactional(readOnly = true)
    public UserReadDto getUserByUsername(String username) {
        log.debug("Getting user by username: {}", username);
        return userRepository.findByUsername(username)
                .map(userReadMapper::map).orElseThrow(() -> new UserNotFoundException("User  with username " + username + " not found"));
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        log.debug("Getting user by email: {}", email);
        return userRepository.findByEmail(email);

    }

    @Transactional(readOnly = true)
    public Optional<User> getByUsername(String username) {
        log.debug("Getting user by username: {}", username);
        return userRepository.findByUsername(username);

    }

    @SneakyThrows
    private void uploadImage(MultipartFile image) {
        if (!image.isEmpty()) {
            imageService.upload(image.getOriginalFilename(), image.getInputStream());
        }
    }

    @Transactional(readOnly = true)
    public Optional<byte[]> findAvatar(String username) {
        log.debug("Finding avatar for user: {}", username);
        return userRepository.findByUsername(username)
                .map(User::getImage)
                .filter(StringUtils::hasText)
                .flatMap(imageService::get);

    }

    @Transactional
    public void updateResetPasswordToken(String token, String email) throws UserNotFoundException {
        log.info("Updating reset password token for email: {}", email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User  with email " + email + " not found"));
        user.setResetPasswordToken(token);
        userRepository.saveAndFlush(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> getByResetPasswordToken(String token) {
        log.debug("Getting user by reset password token: {}", token);
        return userRepository.findByResetPasswordToken(token);
    }

    @Transactional
    public void updatePassword(User user, String newPassword) {

        log.info("Updating password for user: {}", user.getUsername());
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setResetPasswordToken(null);
        userRepository.save(user);

    }

}