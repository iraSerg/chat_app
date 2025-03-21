package com.irina.chat_app.mapper;

import com.irina.chat_app.dto.UserEditDto;
import com.irina.chat_app.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class UserEditMapper implements Mapper<UserEditDto, User> {

    @Override
    public User map(UserEditDto userEditDto, User user) {

        user.setName(userEditDto.getName());
        Optional.ofNullable(userEditDto.getImage())
                .filter(Predicate.not(MultipartFile::isEmpty))
                .ifPresent(image -> user.setImage(image.getOriginalFilename()));
        return user;
    }


}