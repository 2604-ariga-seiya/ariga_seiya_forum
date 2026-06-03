package com.example.ariga_seiya_forum.controller.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginForm {
    @NotBlank(message = "{E0001}")
    private String account;

    @NotBlank(message = "{E0001}")
    private String password;
}
