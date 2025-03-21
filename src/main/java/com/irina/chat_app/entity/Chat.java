package com.irina.chat_app.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "chats")

public class Chat {

    @Id
    private String id;
    private String name;
    private String creator;
    private String inviter;
}