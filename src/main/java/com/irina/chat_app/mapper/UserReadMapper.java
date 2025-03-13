package com.irina.chat_app.mapper;

import com.irina.chat_app.dto.UserReadDto;
import com.irina.chat_app.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserReadMapper implements Mapper<User, UserReadDto> {

    @Override
    public UserReadDto map(User user) {
        return new UserReadDto(user.getUsername(), user.getName(), user.getEmail(), user.getImage());
    }
}
