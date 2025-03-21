package com.irina.chat_app.unit;

import com.irina.chat_app.dto.ChatCreateDto;
import com.irina.chat_app.entity.Invitation;
import com.irina.chat_app.repository.InvitationRepository;
import com.irina.chat_app.service.InvitationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class InvitationServiceTests {
    @Mock
    private InvitationRepository invitationRepository;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private InvitationServiceImpl invitationService;

    private Invitation invitation;
    private ChatCreateDto chatCreateDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        chatCreateDto = new ChatCreateDto("testChat","invitedUser","creatorUser");


        invitation = Invitation.builder()
                .chatId("someChatId")
                .fromUser("creatorUser")
                .toUser("invitedUser")
                .date(LocalDateTime.now())
                .content("Invitation to chat: testChat, from: creatorUser")
                .build();
    }
    @Test
    void findInvitations_shouldReturnListOfInvitations() {
        List<Invitation> invitationList = List.of(invitation);
        when(invitationRepository.findAllByToUser("invitedUser")).thenReturn(invitationList);

        List<Invitation> foundInvitations = invitationService.findInvitations("invitedUser");

        assertEquals(invitationList, foundInvitations);
        verify(invitationRepository, times(1)).findAllByToUser("invitedUser");
    }
    @Test
    void sendInvitation_shouldSaveAndSendInvitation() {

        String chatId = "someChatId";

        ArgumentCaptor<String> toUserCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Invitation> invitationCaptor = ArgumentCaptor.forClass(Invitation.class);

        invitationService.sendInvitation(chatCreateDto, chatId);


        verify(invitationRepository, times(1)).save(any(Invitation.class));

        verify(simpMessagingTemplate, times(1)).convertAndSendToUser(
                toUserCaptor.capture(),
                destinationCaptor.capture(),
                invitationCaptor.capture()
        );

        assertEquals(chatCreateDto.getInviter(), toUserCaptor.getValue());
        assertEquals("/queue/" + chatCreateDto.getInviter() + ".user.invitations", destinationCaptor.getValue());

        Invitation sentInvitation = invitationCaptor.getValue();
        assertNotNull(sentInvitation);
        assertEquals(chatId, sentInvitation.getChatId());
        assertEquals(chatCreateDto.getCreator(), sentInvitation.getFromUser());
        assertEquals(chatCreateDto.getInviter(), sentInvitation.getToUser());
        assertEquals("Invitation to chat: testChat, from: creatorUser", sentInvitation.getContent());
    }
    @Test
    void deleteInvitationByChatId_shouldDeleteInvitation() {
        String chatId = "testChatId";

        invitationService.deleteInvitationByChatId(chatId);

        verify(invitationRepository, times(1)).deleteByChatId(chatId);
    }
}
