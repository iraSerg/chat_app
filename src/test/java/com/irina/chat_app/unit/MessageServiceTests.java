package com.irina.chat_app.unit;

import com.irina.chat_app.dto.MessageCreateDto;
import com.irina.chat_app.entity.Chat;
import com.irina.chat_app.entity.Message;
import com.irina.chat_app.mapper.MessageCreateMapper;
import com.irina.chat_app.repository.MessageRepository;
import com.irina.chat_app.service.MessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

public class MessageServiceTests {
    @Mock
    private MessageRepository messageRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private MessageCreateMapper messageCreateMapper;

    @InjectMocks
    private MessageServiceImpl messageService;

    private MessageCreateDto messageCreateDto;
    private Chat chat;
    private Message message;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        messageCreateDto = new MessageCreateDto("chatId", null,"fromUser","toUser","message");

        chat = new Chat();
        chat.setId("chatId");
        chat.setCreator("creatorUser");
        chat.setInviter("invitedUser");

        message = new Message();
        message.setChatId("chatId");
        message.setFromUser("fromUser");
        message.setToUser("toUser");
        message.setContent("message");
        message.setDate(LocalDateTime.now());
    }
    @Test
    void processAndSendMessage_withInviter_shouldSendMessagesToBothUsers() {
        when(messageCreateMapper.map(messageCreateDto)).thenReturn(message);

        messageService.processAndSendMessage(messageCreateDto, chat);

        verify(messageCreateMapper, times(1)).map(messageCreateDto);
        verify(messageRepository, times(1)).save(any(Message.class));
        verify(messagingTemplate, times(1)).convertAndSendToUser("toUser", "/queue/chatId.private.messages", message);
        verify(messagingTemplate, times(1)).convertAndSendToUser("fromUser", "/queue/chatId.private.messages", message);
    }

    @Test
    void processAndSendMessage_withoutInviter_shouldSendMessageToFromUser() {
        chat.setInviter(null);
        when(messageCreateMapper.map(messageCreateDto)).thenReturn(message);

        messageService.processAndSendMessage(messageCreateDto, chat);

        verify(messageCreateMapper, times(1)).map(messageCreateDto);
        verify(messageRepository, times(1)).save(any(Message.class));
        verify(messagingTemplate, times(1)).convertAndSendToUser("fromUser", "/queue/chatId.private.messages", message);
        verify(messagingTemplate, never()).convertAndSendToUser(eq("toUser"), anyString(), any(Message.class));
    }
}
