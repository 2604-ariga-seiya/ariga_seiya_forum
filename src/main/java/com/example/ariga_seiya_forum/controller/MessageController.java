package com.example.ariga_seiya_forum.controller;

import com.example.ariga_seiya_forum.controller.form.MessageForm;
import com.example.ariga_seiya_forum.controller.form.UserForm;
import com.example.ariga_seiya_forum.service.MessageService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;


@Slf4j
@Controller
public class MessageController {

    @Autowired
    MessageService messageService;

    /*
     * 新規投稿画面表示
     */
    @GetMapping("/new")
    public ModelAndView view(HttpSession session){
        log.info("[MessageController] Received request to display new message page.");

        ModelAndView mav = new ModelAndView();
        // form用の空のentityを準備
        MessageForm messageForm = new MessageForm();
        // 画面遷移先を指定
        mav.setViewName("/new");
        // 準備した空のformを保管
        mav.addObject("formModel", messageForm);

        UserForm loginUser = (UserForm) session.getAttribute("loginUser");

        messageForm.setUserId(loginUser.getId());

        return mav;
    }

    /*
     * 新規投稿処理
     */
    @PostMapping("/addMessage")
    public ModelAndView addMessage(
            @Validated @ModelAttribute("formModel") MessageForm messageForm,
            BindingResult result,
            HttpSession session){

        log.info("[MessageController] Received request to add new message. Content: {}", messageForm.getContent());

        ModelAndView mav = new ModelAndView();

        UserForm loginUser = (UserForm) session.getAttribute("loginUser");

        if(result.hasErrors()){
            log.warn("[MessageController] Validation error occurred on adding message.");

            mav.setViewName("new");
            // 入力エラー情報が含まれたフォームオブジェクトをそのまま画面に送り返す
            mav.addObject("formModel", messageForm);
            return mav;
        }

        messageForm.setUserId(loginUser.getId());

        // 投稿をテーブルに格納
        messageService.saveMessage(messageForm);

        log.info("[MessageController] Message added successfully. Redirecting to top.");

        mav.setViewName("redirect:/top");
        return mav;
    }

    @DeleteMapping("/deleteMessage/{id}")
    public ModelAndView deleteMessage(
            @PathVariable String id, RedirectAttributes redirectAttributes,
            HttpSession session){
        log.info("[MessageController] Received request to delete task - ID: {}", id);

        if (id == null || !id.matches("^[0-9]+$")) {
            log.warn("[MessageController] Invalid ID format: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "不正なパラメータです。");
            return new ModelAndView("redirect:/");
        }

        int messageId = Integer.parseInt(id);
        MessageForm messageForm = messageService.findMessageById(messageId);

        if (messageForm == null) {
            log.warn("[MessageController] Invalid delete access - MessageID: {} does not exist.", id);
            redirectAttributes.addFlashAttribute("errorMessage", "E0025");
            // rootへリダイレクト
            return new ModelAndView("redirect:/top");
        }

        UserForm loginUser = (UserForm) session.getAttribute("loginUser");

        if (loginUser == null || !Objects.equals(messageForm.getUserId(), loginUser.getId())) {
            log.warn("[MessageController] Unauthorized delete attempt! UserID: {} tried to delete MessageID: {} (OwnerID: {})",
                    loginUser != null ? loginUser.getId() : "Guest", messageId, messageForm.getUserId());

            redirectAttributes.addFlashAttribute("errorMessage", "E0025");
            return new ModelAndView("redirect:/top");
        }

        messageService.deleteMessage(messageId);
        // rootへリダイレクト
        return new ModelAndView("redirect:/top");
    }
}
