package com.irina.chat_app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Value;
import lombok.experimental.FieldNameConstants;

@Value
@FieldNameConstants
public class UserCreateDto {
    @NotBlank
    String username;
    @NotBlank
    String name;
    @Email
    @NotBlank
    String email;
    @NotBlank
    @Size(min = 8)
    String password;


}
