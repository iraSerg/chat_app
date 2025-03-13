package com.irina.chat_app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;
import lombok.experimental.FieldNameConstants;
import org.springframework.web.multipart.MultipartFile;

@Value
@FieldNameConstants
public class UserEditDto {
    @NotBlank
    String name;
    MultipartFile image;
}