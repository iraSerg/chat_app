package com.irina.chat_app.repository;

import com.irina.chat_app.entity.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends MongoRepository<Chat,String> {

    List<Chat> findByCreatorOrInviter(String creator, String inviter);

    boolean existsByIdAndInviter(String id, String inviter);


}
