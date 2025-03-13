package com.irina.chat_app.service;

import com.irina.chat_app.dto.MessageCreateDto;
import com.irina.chat_app.dto.MessageReadDto;
import com.irina.chat_app.entity.Chat;
import com.irina.chat_app.entity.Message;
import com.irina.chat_app.mapper.MessageCreateMapper;
import com.irina.chat_app.mapper.MessageReadMapper;
import com.irina.chat_app.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;
    private final MessageCreateMapper messageCreateMapper;
    private final MessageReadMapper messageReadMapper;
    private static final String GREETING_MESSAGE = "Hello!";


    @Override
    public List<MessageReadDto> findAllMessagesFor(String chatId) {
        log.info("Finding all messages for chat ID: {}", chatId);
        List<Message> messages = messageRepository.findMessagesByChatId(chatId);

        return messages.stream().map(messageReadMapper::map).collect(Collectors.toList());
    }

    @Override
    public void deleteAllMessagesByChatId(String chatId) {
        log.info("Deleting all messages for chat ID: {}", chatId);
        messageRepository.deleteAllByChatId(chatId);
    }

    @Override
    public void processAndSendMessage(MessageCreateDto messageCreateDto, Chat chat) {
        Message message =
                Optional.of(messageCreateDto)
                        .map(messageCreateMapper::map)
                        .map(
                                msg -> {
                                    msg.setDate(LocalDateTime.now());
                                    return msg;
                                })
                        .orElseThrow();

        String userQueue = "/queue/" + message.getChatId() + ".private.messages";
        if (chat.getInviter() == null) {
            sendMessageToUser(message.getFromUser(), userQueue, message);
        } else {
            sendMessageToUser(message.getToUser(), userQueue, message);
            sendMessageToUser(message.getFromUser(), userQueue, message);
        }

        saveMessage(message);
    }

    @Override
    public void sendGreeting(Chat chat) {
        log.info("Sending greeting to chat: {}", chat.getId());
        Message message =
                new Message(
                        null,
                        chat.getId(),
                        LocalDateTime.now(),
                        chat.getInviter(),
                        chat.getCreator(),
                        GREETING_MESSAGE);

        String userQueue = "/queue/" + message.getChatId() + ".private.messages";
        sendMessageToUser(message.getFromUser(),userQueue, message);
        sendMessageToUser(message.getToUser(),userQueue, message);
        saveMessage(message);
    }

    private void saveMessage(Message message) {
        log.info("Saving message: {}", message);
        messageRepository.save(message);
    }

    private void sendMessageToUser(String user, String destination, Message message) {
        log.info("Sending message to user: {}, destination: {}", user, destination);
        messagingTemplate.convertAndSendToUser(user, destination, message);
    }

}
