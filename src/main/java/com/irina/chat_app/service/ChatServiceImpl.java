package com.irina.chat_app.service;

import com.irina.chat_app.dto.ChatCreateDto;
import com.irina.chat_app.dto.MessageCreateDto;
import com.irina.chat_app.entity.Chat;
import com.irina.chat_app.exception.*;
import com.irina.chat_app.mapper.ChatCreateMapper;
import com.irina.chat_app.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    private final MessageService messageService;
    private final ChatCreateMapper chatCreateMapper;
    private final InvitationService invitationService;
    private final UserService userService;

    @Override
    public Chat createAndInvite(ChatCreateDto chatCreateDto) {
        log.info("Creating chat");
        userService.getByUsername(chatCreateDto.getInviter()).orElseThrow(()->new UserNotFoundException("Invited user with username " + chatCreateDto.getInviter() + "not found"));
        if(chatCreateDto.getCreator().equals(chatCreateDto.getInviter())){
            throw new SelfInvitationException("User  cannot invite themselves to the chat.");
        }
        Chat chat = chatCreateMapper.map(chatCreateDto);
        Chat createdChat = chatRepository.save(chat);
        invitationService.sendInvitation(chatCreateDto, createdChat.getId());

        return createdChat;
    }

    @Override
    public Chat joinInChat(String chatId, Principal principal) {
        log.info("User {} joining chat {}", principal.getName(), chatId);
        Chat chat =
                chatRepository
                        .findById(chatId)
                        .orElseThrow(() -> new ChatNotFoundException("Chat with id " + chatId + " not found"));
        chat.setInviter(principal.getName());
        Chat savedChat = chatRepository.save(chat);
        messageService.sendGreeting(savedChat);
        invitationService.deleteInvitationByChatId(chatId);
        return savedChat;
    }

    @Override
    public void sendMessage(MessageCreateDto messageCreateDto) {
        Chat chat = chatRepository.findById(messageCreateDto.getChatId())
                .orElseThrow(() -> new ChatNotFoundException("Chat with id " + messageCreateDto.getChatId() + " not found"));

        messageService.processAndSendMessage(messageCreateDto, chat);
    }

    @Override
    public List<Chat> findChatForUser(String username) {
        log.info("Finding chats for user: {}", username);
        return chatRepository.findByCreatorOrInviter(username, username);
    }

    @Override
    public void leaveChat(String chatId, String username) {
        log.info("User {} leaving chat {}", username, chatId);
        Chat chat =
                chatRepository
                        .findById(chatId)
                        .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + chatId));

        boolean isUserInChat = chatRepository.existsByIdAndInviter(chatId, username);
        if (!isUserInChat) {
            throw new UserNotInChatException("You can not leave the chat as you are not inviter.");
        }

        chat.setInviter(null);
        chatRepository.save(chat);
        log.debug("Chat after leaving: {}", chat);
    }


    @Override
    public void deleteChat(String chatId, String username) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + chatId));
        if (!chat.getCreator().equals(username)) {
            throw new UnauthorizedException("You are not authorized to delete this chat.");
        }
        messageService.deleteAllMessagesByChatId(chatId);
        invitationService.deleteInvitationByChatId(chatId);
        chatRepository.deleteById(chatId);
    }
}
