package com.example.ariga_seiya_forum.controller;

import com.example.ariga_seiya_forum.controller.form.*;
import com.example.ariga_seiya_forum.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
public class UserManageController {

    @Autowired
    UserService userService;

    /*
     * ユーザー管理画面表示
     */
    @GetMapping("/user/management")
    public ModelAndView view(HttpSession session){

        log.info("[UserManageController] Received request to display User Management page.");

        ModelAndView mav = new ModelAndView();

        UserForm loginUser = (UserForm) session.getAttribute("loginUser");

        mav.addObject("loginUser", loginUser);

        List<UserForm> userList = userService.findAllUsers();

        // 画面遷移先を指定
        mav.setViewName("user-management");

        mav.addObject("userList", userList);

        String bindingResultKey = org.springframework.validation.BindingResult.MODEL_KEY_PREFIX + "userForm";

        if (session.getAttribute(bindingResultKey) != null) {
            mav.addObject(bindingResultKey, session.getAttribute(bindingResultKey));
            mav.addObject("userForm", session.getAttribute("userForm"));

            // 一度画面に渡したらセッションから削除する
            session.removeAttribute(bindingResultKey);
            session.removeAttribute("userForm");
        } else {
            // 通常アクセス時は、エラー情報の入っていない綺麗な空のフォームを置く
            mav.addObject("userForm", new CommentForm());
        }

        if (session.getAttribute("errorMessage") != null) {
            mav.addObject("errorMessage", session.getAttribute("errorMessage"));
            session.removeAttribute("errorMessage");
        }

        return mav;
    }

    @PostMapping("/user/change-status/{id}/{status}")
    public ModelAndView changeStatus(
            @PathVariable String id,
            RedirectAttributes redirectAttributes,
            @PathVariable String status) {

        log.info("[UserManageController] Received request to change status. UserID: {}, Target Status: {}", id, status);

        if (id == null || !id.matches("^[0-9]+$")) {
            log.warn("[UserManageController] Invalid ID format: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "E0025");
            return new ModelAndView("redirect:/user/management");
        }

        if (status == null || !status.matches("[0-1]")) {
            log.warn("[UserManageController] Invalid status value received: {}", status);
            redirectAttributes.addFlashAttribute("errorMessage", "E0025");
            return new ModelAndView("redirect:/user/management");
        }

        Integer statusInt = Integer.parseInt(status);

        int userId = Integer.parseInt(id);
        UserForm userForm = userService.findUserById(userId);

        if (userForm == null) {
            log.warn("[UserManageController] Invalid edit access - UserID: {} does not exist.", id);
            redirectAttributes.addFlashAttribute("errorMessage", "E0025");
            return new ModelAndView("redirect:/user/management");
        }

        try {
            userService.changeStatus(userId, statusInt);

            log.info("[UserManageController] Successfully changed status for UserID: {} to {}.", id, status);

        } catch (Exception e) {
            log.error("[UserManageController] Failed to change status for UserID: {}.", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "E0025");
        }

        return new ModelAndView("redirect:/user/management");
    }

    @GetMapping("/user/register")
    public ModelAndView view(){

        log.info("[UserManageController] Received request to display user register page.");

        ModelAndView mav = new ModelAndView();

        // form用の空のentityを準備
        UserForm userForm = new UserForm();

        // 画面遷移先を指定
        mav.setViewName("signup");

        // 準備した空のformを保管
        mav.addObject("formModel", userForm);

        return mav;
    }

}
