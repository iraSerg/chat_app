package com.irina.chat_app.integration.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.irina.chat_app.ChatAppApplication;
import com.irina.chat_app.dto.ChatCreateDto;
import com.irina.chat_app.entity.Chat;
import com.irina.chat_app.entity.Invitation;
import com.irina.chat_app.entity.User;
import com.irina.chat_app.integration.TestContainerInitializer;
import com.irina.chat_app.repository.InvitationRepository;
import com.irina.chat_app.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest(classes = ChatAppApplication.class)
@ContextConfiguration(classes = TestContainerInitializer.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class InvitationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InvitationRepository invitationRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void tearDown() {

        userRepository.deleteAll();
        jdbcTemplate.update("DELETE FROM user_chat_app.users_roles");


    }

    @Test
    @WithMockUser(username = "inviterUser")
    void testDeclineInvitationToChat() throws Exception {

        String inviterUsername = "inviterUser";
        String creatorUsername = "creatorUser";

        User creatorUser = createUser(creatorUsername, creatorUsername, "email1@gmail.com", "password");
        User inviterUser = createUser(inviterUsername, inviterUsername, "email2@gmail.com", "password");

        Chat createdChat = createChat("TestChat", inviterUsername, creatorUsername);

        MvcResult result1 = mockMvc.perform(put("/api/invitations/" + createdChat.getId() + "/decline")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
        mockMvc.perform(put("/api/chat/join/" + createdChat.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Optional<Invitation> invitation = invitationRepository.findByChatId(createdChat.getId());
        assertFalse(invitation.isPresent(), "Invitation should be deleted");
    }

    private User createUser(String username, String name, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user = userRepository.save(user);

        return user;
    }

    private Chat createChat(String chatName, String inviter, String creator) throws Exception {
        ChatCreateDto chatCreateDto = new ChatCreateDto(chatName, inviter, creator);
        MvcResult createChatResult = mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatCreateDto)))
                .andExpect(status().isCreated())
                .andReturn();
        String chatResponse = createChatResult.getResponse().getContentAsString();
        Chat chat = objectMapper.readValue(chatResponse, Chat.class);

        return chat;
    }
}
