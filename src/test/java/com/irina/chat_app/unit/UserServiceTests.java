package com.irina.chat_app.unit;

import com.irina.chat_app.dto.UserCreateDto;
import com.irina.chat_app.dto.UserReadDto;
import com.irina.chat_app.entity.Role;
import com.irina.chat_app.entity.User;
import com.irina.chat_app.mapper.UserCreateMapper;
import com.irina.chat_app.mapper.UserReadMapper;
import com.irina.chat_app.repository.RoleRepository;
import com.irina.chat_app.repository.UserRepository;
import com.irina.chat_app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private UserCreateMapper userCreateMapper;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserReadMapper userReadMapper;
    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserCreateDto userCreateDto;
    private UserReadDto userReadDto;
    private Role testRole;
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setUsername("testUser");
        testUser.setPassword("password");
        userReadDto = new UserReadDto("testCreateUser","testCreateUser","test@example.com",null);
        userCreateDto = new UserCreateDto("testCreateUser","testCreateUser","test@example.com","password");
        testRole = new Role();
        testRole.setName("ROLE_USER");
    }
    @Test
    void create_shouldCreateUserAndReturnUserReadDto() {
        when(userCreateMapper.map(userCreateDto)).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userReadMapper.map(testUser)).thenReturn(userReadDto);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(testRole);
        UserReadDto createdUser = userService.create(userCreateDto);

        assertEquals(userReadDto, createdUser);
        verify(userCreateMapper, times(1)).map(userCreateDto);
        verify(userRepository, times(1)).save(testUser);
        verify(userReadMapper, times(1)).map(testUser);
        verify(roleRepository, times(1)).findByName("ROLE_USER");
    }

    @Test
    void updateResetPasswordToken_shouldSetToken_whenUserFound() {
        String token = "resetToken";
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(userRepository.saveAndFlush(testUser)).thenReturn(testUser);

        assertDoesNotThrow(() -> userService.updateResetPasswordToken(token, email));

        assertEquals(token, testUser.getResetPasswordToken());

        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).saveAndFlush(testUser);
    }



    @Test
    void getByResetPasswordToken_shouldReturnUser_whenTokenExists() {
        String token = "resetToken";
        when(userRepository.findByResetPasswordToken(token)).thenReturn(Optional.of(testUser));

        Optional<User> foundUser = userService.getByResetPasswordToken(token);

        assertTrue(foundUser.isPresent());
        assertEquals(testUser, foundUser.get());
        verify(userRepository, times(1)).findByResetPasswordToken(token);
    }



    @Test
    void updatePassword_shouldUpdatePasswordAndClearToken() {
        String newPassword = "newPassword";
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
        when(userRepository.save(testUser)).thenReturn(testUser);

        userService.updatePassword(testUser, newPassword);

        assertEquals(encodedPassword, testUser.getPassword());
        assertNull(testUser.getResetPasswordToken());
        verify(passwordEncoder, times(1)).encode(newPassword);
        verify(userRepository, times(1)).save(testUser);
    }
}