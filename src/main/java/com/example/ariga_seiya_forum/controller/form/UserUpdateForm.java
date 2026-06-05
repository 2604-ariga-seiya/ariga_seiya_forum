package com.example.ariga_seiya_forum.controller.form;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserUpdateForm {

    private Integer id;

    @NotBlank(message = "{E0019}")
    @Size(max = 10, message = "{E0020}")
    private String name;

    @NotBlank(message = "{E0013}")
    @Size(min = 6, max = 20, message = "{E0014}")
    private String account;

    @Size(max = 20, message = "{E0017}")
    private String password;

    private String passwordConfirm;

    @NotNull(message = "{E0021}")
    @Min(value = 1, message = "{E0025}")
    @Max(value = 4, message = "{E0025}")
    private Integer branchId;

    @NotNull(message = "{E0022}")
    @Min(value = 1, message = "{E0025}")
    @Max(value = 4, message = "{E0025}")
    private Integer departmentId;

    private Integer isStopped;
}
