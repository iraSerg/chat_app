package com.irina.chat_app.integration.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.irina.chat_app.ChatAppApplication;
import com.irina.chat_app.dto.ChatCreateDto;
import com.irina.chat_app.entity.Chat;
import com.irina.chat_app.entity.User;
import com.irina.chat_app.integration.TestContainerInitializer;
import com.irina.chat_app.repository.ChatRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest(classes = ChatAppApplication.class)
@ContextConfiguration(classes = TestContainerInitializer.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class ChatControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void tearDown() {
        chatRepository.deleteAll();
        userRepository.deleteAll();
        jdbcTemplate.update("DELETE FROM user_chat_app.users_roles");


    }

    @Test
    @WithMockUser(username = "inviterUser")
    void testLeaveChat() throws Exception {

        String inviterUsername = "inviterUser";
        String creatorUsername = "creatorUser";


        User creatorUser = createUser(creatorUsername, creatorUsername, "email1@gmail.com", "password");
        User inviterUser = createUser(inviterUsername, inviterUsername, "email2@gmail.com", "password");
        Chat createdChat = createChat("TestChat", inviterUsername, creatorUsername);
        mockMvc.perform(put("/api/chat/join/" + createdChat.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/chat/" + createdChat.getId() + "/leave")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());


        Optional<Chat> chatFromDb = chatRepository.findById(createdChat.getId());
        assertTrue(chatFromDb.isPresent());
        Chat chat = chatFromDb.get();

        assertNull(chatFromDb.get().getInviter());
    }

    @Test
    @WithMockUser(username = "creatorUser")
    void testDeleteChat() throws Exception {

        String inviterUsername = "inviterUser";
        String creatorUsername = "creatorUser";

        User creatorUser = createUser(creatorUsername, creatorUsername, "email1@gmail.com", "password");
        User inviterUser = createUser(inviterUsername, inviterUsername, "email2@gmail.com", "password");
        Chat createdChat = createChat("TestChat", inviterUsername, creatorUsername);

        mockMvc.perform(delete("/api/chat/" + createdChat.getId() + "/delete")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Optional<Chat> chatFromDb = chatRepository.findById(createdChat.getId());
        assertFalse(chatFromDb.isPresent());
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

    @Test
    @WithMockUser(username = "creatorUser")
    void testCreateChat() throws Exception {

        String inviterUsername = "inviterUser";
        String creatorUsername = "creatorUser";


        User creatorUser = createUser(creatorUsername, creatorUsername, "email1@gmail.com", "password");
        User inviterUser = createUser(inviterUsername, inviterUsername, "email2@gmail.com", "password");


        ChatCreateDto chatCreateDto = new ChatCreateDto("MyTestChat", inviterUsername, creatorUsername);

        MvcResult result = mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatCreateDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Chat createdChat = objectMapper.readValue(responseBody, Chat.class);


        Optional<Chat> chatFromDb = chatRepository.findById(createdChat.getId());
        assertNotNull(chatFromDb.orElse(null));
        assertEquals("MyTestChat", chatFromDb.get().getName());

    }

    @Test
    @WithMockUser(username = "creatorUser")
    void testGetUserChats() throws Exception {

        String inviterUsername = "inviterUser";
        String creatorUsername = "creatorUser";


        User creatorUser = createUser(creatorUsername, creatorUsername, "email1@gmail.com", "password");
        User inviterUser = createUser(inviterUsername, inviterUsername, "email2@gmail.com", "password");


        ChatCreateDto chatCreateDto = new ChatCreateDto("MyTestChat", inviterUsername, creatorUsername);


        MvcResult createChatResult = mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatCreateDto)))
                .andExpect(status().isCreated())
                .andReturn();
        String chatResponse = createChatResult.getResponse().getContentAsString();
        Chat createdChat = objectMapper.readValue(chatResponse, Chat.class);

        MvcResult result = mockMvc.perform(get("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();


        String responseBody = result.getResponse().getContentAsString();
        List<Chat> userChats = objectMapper.readValue(responseBody, new TypeReference<List<Chat>>() {
        });
        assertNotNull(userChats);
        assertEquals(1, userChats.size());
        assertEquals(createdChat.getId(), ((List<Chat>) userChats).get(0).getId());

    }

    @Test
    @WithMockUser(username = "inviterUser")
    void testJoinChat() throws Exception {

        String inviterUsername = "inviterUser";
        String creatorUsername = "creatorUser";

        User creatorUser = createUser(creatorUsername, creatorUsername, "email1@gmail.com", "password");
        User inviterUser = createUser(inviterUsername, inviterUsername, "email2@gmail.com", "password");

        Chat createdChat = createChat("TestChat", inviterUsername, creatorUsername);


        MvcResult result = mockMvc.perform(put("/api/chat/join/" + createdChat.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Chat joinedChat = objectMapper.readValue(responseBody, Chat.class);

        assertNotNull(joinedChat);
        assertEquals(createdChat.getId(), joinedChat.getId());
        assertEquals(inviterUsername, joinedChat.getInviter());

        Optional<Chat> chatFromDb = chatRepository.findById(createdChat.getId());
        assertTrue(chatFromDb.isPresent());
        assertThat(chatFromDb.get().getInviter()).contains(inviterUsername);
    }

    private User createUser(String username, String name, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        return userRepository.save(user);
    }

}