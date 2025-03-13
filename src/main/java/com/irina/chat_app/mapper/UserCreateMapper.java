package com.irina.chat_app.mapper;

import com.irina.chat_app.entity.User;
import com.irina.chat_app.dto.UserCreateDto;
import com.irina.chat_app.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserCreateMapper implements Mapper<UserCreateDto, User> {
    private final PasswordEncoder passwordEncoder;

    @Override
    public User map(UserCreateDto userCreateDto) {
        User user = new User();

        user.setName(userCreateDto.getName());
        user.setUsername(userCreateDto.getUsername());
        user.setPassword(passwordEncoder.encode(userCreateDto.getPassword()));
        user.setEmail(userCreateDto.getEmail());


        return user;
    }


}
