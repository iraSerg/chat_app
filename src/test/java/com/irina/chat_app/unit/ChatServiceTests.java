package com.irina.chat_app.unit;

import com.irina.chat_app.dto.ChatCreateDto;
import com.irina.chat_app.entity.Chat;
import com.irina.chat_app.entity.User;
import com.irina.chat_app.exception.SelfInvitationException;
import com.irina.chat_app.exception.UserNotFoundException;
import com.irina.chat_app.mapper.ChatCreateMapper;
import com.irina.chat_app.repository.ChatRepository;
import com.irina.chat_app.service.ChatServiceImpl;
import com.irina.chat_app.service.InvitationService;
import com.irina.chat_app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ChatServiceTests {
    @Mock
    private InvitationService invitationService;

    @Mock
    private UserService userService;

    private Chat chat;

    private ChatCreateDto chatCreateDto;

    @Mock
    ChatCreateMapper chatCreateMapper;
    @Mock
    private ChatRepository chatRepository;
    @InjectMocks
    private ChatServiceImpl chatService;


    @BeforeEach
    void setUp() {
        chat = new Chat();
        chat.setName("TestChat");
        chat.setCreator("creator");
        chatCreateDto = new ChatCreateDto("TestChat","invitedUser","creator");
        MockitoAnnotations.initMocks(this);
    }
    @Test
    void createAndInvite_shouldCreateChatAndSendInvitation() {

        when(chatCreateMapper.map(chatCreateDto)).thenReturn(chat);
        when(chatRepository.save(chat)).thenReturn(chat);
        when(userService.getByUsername("invitedUser")).thenReturn(Optional.of(new User()));
        Chat createdChat = chatService.createAndInvite(chatCreateDto);

        assertEquals(chat, createdChat);
        verify(chatCreateMapper, times(1)).map(chatCreateDto);
        verify(chatRepository, times(1)).save(chat);
        verify(invitationService, times(1)).sendInvitation(chatCreateDto, chat.getId());
    }
    @Test
    void findChatForUser_shouldReturnListOfChats() {
        List<Chat> chatList = List.of(chat);
        when(chatRepository.findByCreatorOrInviter("testUser", "testUser")).thenReturn(chatList);

        List<Chat> foundChats = chatService.findChatForUser("testUser");

        assertEquals(chatList, foundChats);
        verify(chatRepository, times(1)).findByCreatorOrInviter("testUser", "testUser");
    }
    @Test
    void leaveChat_shouldSetInviterToNull() {
        when(chatRepository.findById("someChatId")).thenReturn(Optional.of(chat));
        when(chatRepository.existsByIdAndInviter("someChatId", "invitedUser")).thenReturn(true);
        when(chatRepository.save(chat)).thenReturn(chat);

        chatService.leaveChat("someChatId", "invitedUser");

        assertNull(chat.getInviter());
        verify(chatRepository, times(1)).findById("someChatId");
        verify(chatRepository, times(1)).existsByIdAndInviter("someChatId", "invitedUser");
        verify(chatRepository, times(1)).save(chat);

    }
    @Test
    void createAndInvite_shouldThrowException_whenInvitedUserNotFound() {
        when(userService.getByUsername(chatCreateDto.getInviter())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> chatService.createAndInvite(chatCreateDto));

        verify(chatCreateMapper, never()).map(any());
        verify(chatRepository, never()).save(any());
        verify(invitationService, never()).sendInvitation(any(), anyString());
        verify(userService).getByUsername(chatCreateDto.getInviter());
    }
    @Test
    void createAndInvite_shouldThrowException_whenCreatorEqualsInviter() {
        ChatCreateDto userDto = new ChatCreateDto("TestChat", "creator", "creator");
        when(userService.getByUsername(anyString())).thenReturn(Optional.of(new User()));
        assertThrows(SelfInvitationException.class, () -> chatService.createAndInvite(userDto));

        verify(chatCreateMapper, never()).map(any());
        verify(chatRepository, never()).save(any());
        verify(invitationService, never()).sendInvitation(any(), anyString());
    }

}
