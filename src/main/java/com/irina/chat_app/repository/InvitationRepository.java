package com.irina.chat_app.repository;

import com.irina.chat_app.entity.Invitation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends MongoRepository<Invitation, String> {

    List<Invitation> findAllByToUser(String toUser);

    void deleteByChatId(String chatId);
    Optional<Invitation> findByChatId(String chatId);
}
