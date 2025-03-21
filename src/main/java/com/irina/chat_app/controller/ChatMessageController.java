package com.irina.chat_app.controller;

import com.irina.chat_app.dto.MessageCreateDto;
import com.irina.chat_app.dto.MessageReadDto;
import com.irina.chat_app.service.ChatService;
import com.irina.chat_app.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/chat")
public class ChatMessageController {
    private final MessageService messageService;
    private final ChatService chatService;
    @GetMapping("/{chatId}/old.messages")
    public ResponseEntity<List<MessageReadDto>> listOldMessagesFromUser(@PathVariable String chatId) {
        log.debug("Listing old messages for chat: {}", chatId);
        List<MessageReadDto> messages = messageService.findAllMessagesFor(chatId);
        return ResponseEntity.ok(messages);
    }

    @MessageMapping("/send.message")
    public void sendMessage(@Payload MessageCreateDto message) {
        log.debug("Sending message to chat {} from user {}", message.getChatId(), message.getFromUser());
        chatService.sendMessage(message);
    }

}
