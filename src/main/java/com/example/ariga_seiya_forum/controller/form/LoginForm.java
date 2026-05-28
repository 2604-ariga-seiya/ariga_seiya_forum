package com.example.ariga_seiya_forum.controller.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginForm {
    @NotBlank(message = "{E0001}")
//    @Size(max = 20, message = "{E0014}")
//    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "{E0014}")
    private String account;

    @NotBlank(message = "{E0001}")
//    @Size(min = 6, max = 20, message = "{E0017}")
//    @Pattern(regexp = "^[\\x20-\\x7E]*$", message = "{E0017}")
    private String password;
}
