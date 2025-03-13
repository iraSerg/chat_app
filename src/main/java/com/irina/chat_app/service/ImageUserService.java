package com.irina.chat_app.service;

import com.irina.chat_app.exception.ImageUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

@Service
@Slf4j
public class ImageUserService {
    @Value("${app.image.bucket}")
    private String bucket;

    public void upload(String imagePath, InputStream content) {
        Path fullImagePath = Path.of(bucket, imagePath);
        try (content) {
            Files.createDirectories(fullImagePath.getParent());
            Files.write(fullImagePath, content.readAllBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            log.info("Successfully uploaded image to: {}", fullImagePath);
        } catch (IOException e) {
            log.error("Failed to upload image to: {}", fullImagePath, e);
            throw new ImageUploadException("Failed to upload image to " + fullImagePath, e);
        }
    }

    public Optional<byte[]> get(String imagePath) {
        Path fullImagePath = Path.of(bucket, imagePath);

        if (!Files.exists(fullImagePath)) {
            log.warn("Image not found at path: {}", fullImagePath);
            return Optional.empty();
        }

        try {
            byte[] imageBytes = Files.readAllBytes(fullImagePath);
            log.debug("Successfully retrieved image from: {}", fullImagePath);
            return Optional.of(imageBytes);
        } catch (IOException e) {
            log.error("Failed to read image from: {}", fullImagePath, e);
            return Optional.empty();
        }

    }
}
