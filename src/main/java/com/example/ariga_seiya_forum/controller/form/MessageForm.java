package com.example.ariga_seiya_forum.controller.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageForm {
    private Integer id;
    @NotBlank(message = "{E0006}")
    @Size(max = 30, message = "{E0009}")
    private String title;
    @NotBlank(message = "{E0007}")
    @Size(max = 1000, message = "{E0010}")
    private String content;
    @NotBlank(message = "{E0008}")
    @Size(max = 10, message = "{E0011}")
    private String category;
    private Integer userId;
    private String name;    // ユーザー名（users.name）用
    private String account; // アカウント名（users.account）用
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
