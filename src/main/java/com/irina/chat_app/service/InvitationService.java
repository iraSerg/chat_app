package com.irina.chat_app.service;

import com.irina.chat_app.dto.ChatCreateDto;
import com.irina.chat_app.entity.Invitation;

import java.util.List;

public interface InvitationService {
    List<Invitation> findInvitations(String user);

    void deleteInvitationByChatId(String chatId);

    void declineInvitation(String chatId);

    void sendInvitation(ChatCreateDto chatCreateDto, String chatId);
}
