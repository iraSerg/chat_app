package com.irina.chat_app.exception;

import java.io.IOException;

public class ImageUploadException extends RuntimeException {
    public ImageUploadException(String message) {
        super(message);
    }

    public ImageUploadException(String message, IOException e) {
        super(message, e);
    }
}
