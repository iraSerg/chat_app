package com.irina.chat_app.validator;

import com.irina.chat_app.dto.UserCreateDto;
import com.irina.chat_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class UserRegistrationValidator implements Validator {

    private final UserService userService;
    private final MessageSource messageSource;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserCreateDto.class.equals(clazz);
    }

    @Override
    public void validate(Object targetUser, Errors errors) {
        UserCreateDto newUser = (UserCreateDto) targetUser;
        if (userService.getByUsername(newUser.getUsername()).isPresent()) {
            errors.rejectValue("username", "registration.username.already.exists",
                    messageSource.getMessage("registration.username.already.exists", null, Locale.getDefault()));
        }
        if (userService.getUserByEmail(newUser.getEmail()).isPresent()) {
            errors.rejectValue("email", "registration.email.already.exists",
                    messageSource.getMessage("registration.email.already.exists", null, Locale.getDefault()));
        }
    }
}
