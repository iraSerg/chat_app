package com.irina.chat_app.handler;

import com.irina.chat_app.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.Locale;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class UserExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFound(UserNotFoundException ex, Model model, Locale locale) {
        log.warn("UserNotFoundException: {}", ex.getMessage());
        String errorMessage = messageSource.getMessage("user.not.found", new Object[]{ex.getMessage()}, locale);
        model.addAttribute("errorMessage", errorMessage);
        return "errorPage";
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalState(IllegalStateException ex, Model model, Locale locale) {
        log.error("IllegalStateException: {}", ex.getMessage());
        String errorMessage = messageSource.getMessage("user.creation.failed", null, locale);
        model.addAttribute("errorMessage", errorMessage);
        return "errorPage";
    }
    @ExceptionHandler (IOException.class)
    public String handleIOException(IOException ex, Model model, Locale locale) {
        log.error("IOException: {}", ex.getMessage());
        String errorMessage = messageSource.getMessage("load.image.failed", null, locale);
        model.addAttribute("errorMessage", errorMessage);
        return "errorPage";
    }

}