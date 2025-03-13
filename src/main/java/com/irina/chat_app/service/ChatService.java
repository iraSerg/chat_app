package com.irina.chat_app.service;

import com.irina.chat_app.dto.ChatCreateDto;
import com.irina.chat_app.dto.MessageCreateDto;
import com.irina.chat_app.entity.Chat;

import java.security.Principal;
import java.util.List;

public interface ChatService {
     Chat joinInChat(String chatId,Principal principal);
     Chat createAndInvite(ChatCreateDto chatCreateDto);
     void sendMessage(MessageCreateDto messageCreateDto);
     List<Chat> findChatForUser(String username);
     void deleteChat(String chatId,String username);
     void leaveChat(String chatId, String username);

}
