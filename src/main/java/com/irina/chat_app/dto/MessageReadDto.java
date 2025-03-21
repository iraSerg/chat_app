package com.irina.chat_app.dto;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class MessageReadDto {
    LocalDateTime date;
    String fromUser ;
    String content;
}
