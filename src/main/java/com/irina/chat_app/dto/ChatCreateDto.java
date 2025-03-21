package com.irina.chat_app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;
@Value
public class ChatCreateDto {
    @NotBlank
    String name;

    @NotBlank
    String inviter;

    @NotBlank
    String creator;
}
