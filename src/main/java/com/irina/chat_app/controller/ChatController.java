package com.irina.chat_app.controller;


import com.irina.chat_app.dto.ChatCreateDto;
import com.irina.chat_app.entity.Chat;
import com.irina.chat_app.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/api/chat")

public class ChatController {
    private final ChatService chatService;


    @PutMapping("/join/{chatId}")
    public ResponseEntity<Chat> join(@PathVariable String chatId, Principal principal) {
        log.info("User {} joining chat with ID: {}", principal.getName(), chatId);
        Chat chat = chatService.joinInChat(chatId, principal);
        return ResponseEntity.ok(chat);
    }

    @PostMapping
    public ResponseEntity<?> createChat(@Validated @RequestBody ChatCreateDto chatCreateDto, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {

            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());


            return ResponseEntity.badRequest().body(errors);
        }

        log.info("User  {} created a chat with name {}", principal.getName(), chatCreateDto.getName());
        Chat createdChat = chatService.createAndInvite(chatCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChat);
    }

    @GetMapping
    public ResponseEntity<List<Chat>> getUserChats(Principal principal) {
        List<Chat> chats = chatService.findChatForUser(principal.getName());
        return ResponseEntity.ok(chats);
    }


    @PutMapping("/{chatId}/leave")
    public ResponseEntity<Void> leaveChat(@PathVariable String chatId, Principal principal) {
        String username = principal.getName();
        log.info("User {} leaving chat with ID: {}", username, chatId);
        chatService.leaveChat(chatId, username);
        return ResponseEntity.noContent().build();

    }

    @DeleteMapping("/{chatId}/delete")
    public ResponseEntity<Void> deleteChat(@PathVariable String chatId, Principal principal) {
        log.info("Chat {} deleted",chatId);
        chatService.deleteChat(chatId, principal.getName());
        return ResponseEntity.noContent().build();
    }

}
