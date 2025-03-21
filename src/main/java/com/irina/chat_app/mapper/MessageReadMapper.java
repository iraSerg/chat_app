package com.irina.chat_app.mapper;

import com.irina.chat_app.dto.MessageReadDto;
import com.irina.chat_app.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageReadMapper implements Mapper<Message, MessageReadDto> {

    @Override
    public MessageReadDto map(Message message) {
        return new MessageReadDto(message.getDate(), message.getFromUser(), message.getContent());
    }
}
