package com.irina.chat_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class ResetPasswordDto {
    @NotNull
    String token;
    @NotBlank
    @Size(min = 8)
    String password;
}
