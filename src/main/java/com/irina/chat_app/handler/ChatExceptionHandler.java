package com.irina.chat_app.handler;

import com.irina.chat_app.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ChatExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(ChatNotFoundException.class)
    public ResponseEntity<String> handleChatNotFound(ChatNotFoundException ex, Locale locale) {
        log.warn("ChatNotFoundException: {}", ex.getMessage());
        String errorMessage = messageSource.getMessage("chat.not.found", new Object[]{ex.getMessage()}, locale);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @ExceptionHandler(UserNotInChatException.class)
    public ResponseEntity<String> handleUserNotInChat(UserNotInChatException ex, Locale locale) {
        log.warn("UserNotInChatException: {}", ex.getMessage());
        String errorMessage = messageSource.getMessage("user.not.in.chat", new Object[]{ex.getMessage()}, locale);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage);
    }
    @ExceptionHandler(SelfInvitationException.class)
    public ResponseEntity<String> handleSelfInvitationException(SelfInvitationException ex,Locale locale) {
        log.warn("SelfInvitationException: {}", ex.getMessage());
        String errorMessage = messageSource.getMessage("error.self.invitation", new Object[]{ex.getMessage()}, locale);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundChat(UserNotFoundException ex, Locale locale) {
        log.warn("UserNotFoundException: {}", ex.getMessage());
        String errorMessage = messageSource.getMessage("user.not.found", new Object[]{ex.getMessage()}, locale);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorized(UnauthorizedException ex, Locale locale) {
        log.warn("UnauthorizedException: {}", ex.getMessage());
        String errorMessage = messageSource.getMessage("user.unauthorized", new Object[]{ex.getMessage()}, locale);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex, Locale locale) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        String errorMessage = messageSource.getMessage("error.general", null, locale);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
    }
}