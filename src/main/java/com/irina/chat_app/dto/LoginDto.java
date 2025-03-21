package com.irina.chat_app.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Value;

@Value
public class LoginDto {

@NotEmpty
    String username;
@NotEmpty
    String password;
}
