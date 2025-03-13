package com.irina.chat_app.repository;

import com.irina.chat_app.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {

    List<Message> findMessagesByChatId(String chatId);

    void deleteAllByChatId(String chatId);
}
