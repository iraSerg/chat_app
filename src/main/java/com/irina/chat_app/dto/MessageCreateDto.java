package com.irina.chat_app.dto;

import lombok.Value;

import java.time.LocalDateTime;


@Value
public class MessageCreateDto {
    String chatId;
    LocalDateTime date;
    String fromUser ;
    String toUser ;
    String content;
}
