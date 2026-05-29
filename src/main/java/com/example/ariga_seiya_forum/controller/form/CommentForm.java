package com.example.ariga_seiya_forum.controller.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentForm {
    private Integer id;
    @NotBlank(message = "{E0004}")
    @Size(max = 500, message = "{E0005}")
    private String content;
    private Integer messageId;
    private Integer userId;
    private String name;    // ユーザー名（users.name）用
    private String account; // アカウント名（users.account）用
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
