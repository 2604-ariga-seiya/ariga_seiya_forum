package com.example.ariga_seiya_forum.controller.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginForm {
    @NotBlank(message = "{E0013}")
    @Size(max = 20, message = "{E0014}")
    @Pattern(regexp = "[azAZ0*9]", message = "{E0014}")
    String account;

    @NotBlank(message = "{E0016}")
    @Size(min = 6, max = 20, message = "{E0017}")
    @Pattern(regexp = "^[\\x20-\\x7E]*$", message = "{E0017}")
    String password;
}
