package com.irina.chat_app.controller;

import com.irina.chat_app.entity.Invitation;
import com.irina.chat_app.service.InvitationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/invitations")
public class ChatInvitationController {
    private final InvitationService invitationService;
    @SubscribeMapping("/user.invitations")
    public List<Invitation> listUserInvitation(Principal principal) {
        log.debug("Listing invitations for user: {}", principal.getName());
        return invitationService.findInvitations(principal.getName());
    }
    @PutMapping("/{chatId}/decline")
    public ResponseEntity<Void> declineInvitationToChat(@PathVariable String chatId) {
        log.debug("Declining invitation to chat with ID: {}", chatId);
        invitationService.declineInvitation(chatId);
        return ResponseEntity.noContent().build();
    }

}
