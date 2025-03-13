package com.irina.chat_app.mapper;

import com.irina.chat_app.dto.MessageCreateDto;
import com.irina.chat_app.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageCreateMapper implements Mapper<MessageCreateDto, Message> {
    @Override
    public Message map(MessageCreateDto messageCreateDto) {

        return new Message(
                null,
                messageCreateDto.getChatId(),
                messageCreateDto.getDate(),
                messageCreateDto.getFromUser(),
                messageCreateDto.getToUser(),
                messageCreateDto.getContent()
        );

    }
}
