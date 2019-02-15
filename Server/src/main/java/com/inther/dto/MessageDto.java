package com.inther.dto;

import com.inther.assets.validators.RequestDataValidator;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.UUID;

@Data
public class MessageDto implements Serializable
{
    @Null(groups = {RequestDataValidator.AddMessage.class})
    @NotNull(groups = {RequestDataValidator.UpdateMessage.class})
    private UUID id;

    @NotNull(groups = {RequestDataValidator.AddMessage.class})
    private UUID presentationId;

    @NotNull(groups = {RequestDataValidator.AddMessage.class})
    private UUID userId;

    @Email
    @NotBlank(groups = {RequestDataValidator.AddMessage.class})
    private String email;

    @NotBlank(groups = {RequestDataValidator.AddMessage.class})
    private String text;

    @NotBlank(groups = {RequestDataValidator.AddMessage.class})
    private String type;

    @NotNull(groups = {RequestDataValidator.AddMessage.class})
    private Boolean anonymous;
}