package com.irina.chat_app.mapper;

import com.irina.chat_app.dto.ChatCreateDto;
import com.irina.chat_app.entity.Chat;
import org.springframework.stereotype.Component;

@Component
public class ChatCreateMapper implements Mapper<ChatCreateDto, Chat> {

    @Override
    public Chat map(ChatCreateDto chatCreateDto) {
        Chat chat = new Chat();
        chat.setName(chatCreateDto.getName());
        chat.setCreator(chatCreateDto.getCreator());
        return chat;
    }
}
