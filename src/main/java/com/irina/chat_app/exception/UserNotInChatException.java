package com.irina.chat_app.exception;

public class UserNotInChatException extends RuntimeException{
    public UserNotInChatException(String message) {
        super(message);
    }
}
