package com.irina.chat_app.service;

import com.irina.chat_app.dto.ChatCreateDto;
import com.irina.chat_app.entity.Invitation;
import com.irina.chat_app.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {
    private final InvitationRepository invitationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public List<Invitation> findInvitations(String user) {
        return invitationRepository.findAllByToUser(user);
    }

    public void deleteInvitationByChatId(String chatId) {
        invitationRepository.deleteByChatId(chatId);
    }

    public void declineInvitation(String chatId) {
        invitationRepository.deleteByChatId(chatId);
    }

    public void sendInvitation(ChatCreateDto chatCreateDto, String chatId) {

        Invitation invitation = Invitation.builder()
                .chatId(chatId)
                .fromUser(chatCreateDto.getCreator())
                .toUser(chatCreateDto.getInviter())
                .date(LocalDateTime.now())
                .content("Invitation to chat: %s, from: %s".formatted(chatCreateDto.getName(), chatCreateDto.getCreator()))
                .build();
        invitationRepository.save(invitation);

        simpMessagingTemplate.convertAndSendToUser(
                chatCreateDto.getInviter(),
                "/queue/" + chatCreateDto.getInviter() + ".user.invitations",
                invitation);


    }
}
