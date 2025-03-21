package com.irina.chat_app.service;

import com.irina.chat_app.dto.MessageCreateDto;
import com.irina.chat_app.dto.MessageReadDto;
import com.irina.chat_app.entity.Chat;
import com.irina.chat_app.entity.Message;

import java.util.List;

public interface MessageService {
    List<MessageReadDto> findAllMessagesFor(String chatRoomId);

    void deleteAllMessagesByChatId(String chatId);

    void processAndSendMessage(MessageCreateDto messageCreateDto, Chat chat);

    void sendGreeting(Chat chat);
}
