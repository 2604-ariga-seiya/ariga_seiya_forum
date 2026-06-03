package com.example.ariga_seiya_forum.controller;

import com.example.ariga_seiya_forum.controller.form.UserForm;
import com.example.ariga_seiya_forum.controller.form.UserUpdateForm;
import com.example.ariga_seiya_forum.exception.AccountDuplicateException;
import com.example.ariga_seiya_forum.exception.InvalidDepartmentException;
import com.example.ariga_seiya_forum.exception.PasswordMismatchException;
import com.example.ariga_seiya_forum.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class UserEditController {

    @Autowired
    UserService userService;

    @GetMapping("/user/edit/{id}")
    public ModelAndView view(
            @PathVariable String id,
            RedirectAttributes redirectAttributes){

        log.info("[UserEditController] Requested edit for UserID: {}", id);

        if (id == null || !id.matches("^[0-9]+$")) {
            log.warn("[UserEditController] Invalid ID format: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "E0025");
            return new ModelAndView("redirect:/user/management");
        }

        ModelAndView mav = new ModelAndView();
        int userId = Integer.parseInt(id);
        UserForm userForm = userService.findUserById(userId);

        if (userForm == null) {
            log.warn("[UserEditController] Invalid edit access - UserID: {} does not exist.", id);
            redirectAttributes.addFlashAttribute("errorMessage", "E0025");
            return new ModelAndView("redirect:/user/management");
        }

        mav.setViewName("edit");
        // 投稿データオブジェクトを保管
        mav.addObject("formModel", userForm);

        log.info("[UserEditController] Edit screen displayed. UserID: {}", id);

        return mav;
    }

    /*
     * 編集反映処理
     */
    @PutMapping("/user/update/{id}")
    public ModelAndView updateUser(
            @PathVariable String id,
            @Validated @ModelAttribute("formModel") UserUpdateForm userUpdateForm,
            BindingResult result,
            RedirectAttributes redirectAttributes){

        log.info("[UserEditController] Received request to update user. UserID: {}, New Account: {}, New Name: {}",
                id, userUpdateForm.getAccount(), userUpdateForm.getName());

        if (id == null || !id.matches("^[0-9]+$")) {
            log.warn("[UserEditController] Invalid ID format: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "E0025");
            return new ModelAndView("redirect:/user/management");
        }

        ModelAndView mav = new ModelAndView();

        if(result.hasErrors()){
            log.warn("[UserEditController] Validation error occurred on editing user.");

            List<String> errorMessages = new ArrayList<>();
            for (FieldError error : result.getFieldErrors()) {
                errorMessages.add(error.getDefaultMessage());
            }

            mav.addObject("errorMessages", errorMessages);
            mav.setViewName("edit");
            return mav;
        }

        // UrlParameterのidを更新するentityにセット
        int userId = Integer.parseInt(id);
        userUpdateForm.setId(userId);

        try{
            // 編集した投稿を更新
            userService.updateUser(userUpdateForm);
            log.info("[UserEditController] User update successful. Redirecting to user management page. ID: {}", id);

        } catch (PasswordMismatchException e) {
            log.warn("[SignUpController] Update failed for account: {}. Reason: {}", userUpdateForm.getAccount(), e.getMessage());

            mav.addObject("errorMessage", "E0018");

            mav.setViewName("signup");
            return mav;

        } catch (InvalidDepartmentException e){
            log.warn("[SignUpController] Update failed for account: {}. Reason: {}", userUpdateForm.getAccount(), e.getMessage());

            mav.addObject("errorMessage", "E0023");

            mav.setViewName("signup");
            return mav;
        } catch (AccountDuplicateException e){
            log.warn("[SignUpController] SignUp failed for account: {}. Reason: {}", userUpdateForm.getAccount(), e.getMessage());

            mav.addObject("errorMessage", "E0015");

            mav.setViewName("signup");
            return mav;
        } catch (IllegalArgumentException e){
            log.warn("[UserEditController] Update failed. Internal error: {}", e.getMessage());

            mav.setViewName("edit");
            mav.addObject("errorMessage", "E0025");

            return mav;
        }

        // rootへリダイレクト
        return new ModelAndView("redirect:/user/management");
    }
}
