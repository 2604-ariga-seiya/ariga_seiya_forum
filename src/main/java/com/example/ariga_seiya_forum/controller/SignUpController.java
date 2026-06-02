package com.example.ariga_seiya_forum.controller;

import com.example.ariga_seiya_forum.controller.form.LoginForm;
import com.example.ariga_seiya_forum.controller.form.UserForm;
import com.example.ariga_seiya_forum.exception.InvalidDepartmentException;
import com.example.ariga_seiya_forum.exception.PasswordMismatchException;
import com.example.ariga_seiya_forum.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class SignUpController {

    @Autowired
    UserService userService;

    @PostMapping("/signup")
    public ModelAndView signUpUser(
            @Validated @ModelAttribute("formModel") UserForm userForm,
            BindingResult result,
            HttpSession session){

        log.info("[SignUpController] Signup request received. Account: {}", userForm.getAccount());

        ModelAndView mav = new ModelAndView();

        if(result.hasErrors()){
            log.warn("[SignUpController] SignUp rejected due to validation errors.");

            List<String> errorMessages = new ArrayList<>();
            for (FieldError error : result.getFieldErrors()) {
                errorMessages.add(error.getDefaultMessage());
            }

            mav.addObject("errorMessages", errorMessages);
            mav.setViewName("signup");
            return mav;
        }

        try {
            userService.registerUser(userForm);
            return new ModelAndView("redirect:/user/management");

        } catch (PasswordMismatchException e) {
            log.warn("[SignUpController] SignUp failed for account: {}. Reason: {}", userForm.getAccount(), e.getMessage());

            mav.addObject("errorMessage", "E0018");

            mav.setViewName("signup");
            return mav;

        } catch (InvalidDepartmentException e){
            log.warn("[SignUpController] SignUp failed for account: {}. Reason: {}", userForm.getAccount(), e.getMessage());

            mav.addObject("errorMessage", "E0023");

            mav.setViewName("signup");
            return mav;
        }
    }
}
