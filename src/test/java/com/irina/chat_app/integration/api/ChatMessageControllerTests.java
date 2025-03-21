package com.irina.chat_app.integration.api;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.irina.chat_app.ChatAppApplication;
import com.irina.chat_app.dto.ChatCreateDto;
import com.irina.chat_app.dto.MessageReadDto;
import com.irina.chat_app.entity.Chat;
import com.irina.chat_app.entity.Message;
import com.irina.chat_app.entity.User;
import com.irina.chat_app.integration.TestContainerInitializer;
import com.irina.chat_app.repository.MessageRepository;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
@SpringBootTest(classes = ChatAppApplication.class)
@ContextConfiguration(classes = TestContainerInitializer.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class ChatMessageControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void tearDown() {

        userRepository.deleteAll();
        jdbcTemplate.update("DELETE FROM user_chat_app.users_roles");


    }

    @Test
    @WithMockUser(username = "testUser")
    void testListOldMessagesFromUser() throws Exception {

        String testUsername = "testUser";
        String creatorUsername = "creatorUser";

        User testUser = createUser(testUsername, testUsername, "email1@gmail.com", "password");
        User creatorUser = createUser(creatorUsername, creatorUsername, "email2@gmail.com", "password");

        Chat createdChat = createChat("TestChat", testUsername, creatorUsername);

        Message message1 = createMessage(createdChat.getId(), testUsername, "Hello!");
        Message message2 = createMessage(createdChat.getId(), creatorUsername, "Hi there!");

        MvcResult result = mockMvc.perform(get("/api/chat/" + createdChat.getId() + "/old.messages")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();


        String responseBody = result.getResponse().getContentAsString();
        List<MessageReadDto> messages = objectMapper.readValue(responseBody, new TypeReference<List<MessageReadDto>>() {
        });

        assertNotNull(messages);
        assertEquals(2, messages.size());

        MessageReadDto messageReadDto1 = messages.get(0);
        assertEquals(message1.getContent(), messageReadDto1.getContent());

        MessageReadDto messageReadDto2 = messages.get(1);
        assertEquals(message2.getContent(), messageReadDto2.getContent());
    }


    private Message createMessage(String chatId, String fromUser, String text) {
        Message message = new Message();
        message.setChatId(chatId);
        message.setFromUser(fromUser);
        message.setContent(text);
        message.setDate(LocalDateTime.now());
        message = messageRepository.save(message);
        return message;
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