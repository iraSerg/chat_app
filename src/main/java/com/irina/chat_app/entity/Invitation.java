package com.irina.chat_app.entity;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Data
@Document(collection = "invitations")
@Builder
public class Invitation {
    @Id
    private String id;
    private String chatId;
    private String fromUser;
    private String toUser;
    private LocalDateTime date;
    private String content;
}
