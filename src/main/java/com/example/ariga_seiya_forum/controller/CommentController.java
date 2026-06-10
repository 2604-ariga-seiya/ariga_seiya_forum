package com.example.ariga_seiya_forum.controller;

import com.example.ariga_seiya_forum.controller.form.CommentForm;
import com.example.ariga_seiya_forum.controller.form.UserForm;
import com.example.ariga_seiya_forum.service.CommentService;
import com.example.ariga_seiya_forum.service.MessageService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Slf4j
@Controller
public class CommentController {
    @Autowired
    CommentService commentService;
    @Autowired
    MessageService messageService;

    @PostMapping("/comment")
    public ModelAndView addComment(
            @Validated @ModelAttribute("commentForm") CommentForm commentForm,
            BindingResult result,
            HttpSession session) {

        ModelAndView mav = new ModelAndView();

        UserForm loginUser = (UserForm) session.getAttribute("loginUser");

        if (result.hasErrors()) {
            log.warn("[CommentController] Validation error occurred.");

            String key = BindingResult.MODEL_KEY_PREFIX + "commentForm";
            session.setAttribute(key, result);

            session.setAttribute("commentForm", commentForm);

            mav.setViewName("redirect:/top");
            return mav;
        }

        try {
            if (loginUser != null) {
                commentForm.setUserId(loginUser.getId());
            }

            commentService.saveComment(commentForm);
            mav.setViewName("redirect:/top");
        } catch (IllegalArgumentException e) {
            log.warn("[CommentController] Add failed: {}", e.getMessage());

            mav.addObject("errorMessage", "E0025");

            session.setAttribute("errorMessage", "E0025");

            mav.setViewName("redirect:/top");
        }

        return mav;
    }

    /*
     * 返信削除処理
     */
    @DeleteMapping("/comment/delete/{id}")
    public ModelAndView deleteComment(
            @PathVariable String id,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        log.info("[CommentController] Received request to delete comment - ID: {}", id);

        if (id == null || !id.matches("^[0-9]+$")) {
            log.warn("[CommentController] Invalid ID format: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "E0025");
            return new ModelAndView("redirect:/top");
        }

        int commentId = Integer.parseInt(id);
        CommentForm commentForm = commentService.findCommentById(commentId);

        if (commentForm == null) {
            log.warn("[CommentController] Invalid delete access - MessageID: {} does not exist.", id);
            redirectAttributes.addFlashAttribute("errorMessage", "E0025");
            // rootへリダイレクト
            return new ModelAndView("redirect:/top");
        }

        UserForm loginUser = (UserForm) session.getAttribute("loginUser");

        if (loginUser == null || !Objects.equals(commentForm.getUserId(), loginUser.getId())) {
            log.warn("[CommentController] Unauthorized delete attempt! UserID: {} tried to delete CommentID: {} (OwnerID: {})",
                    loginUser != null ? loginUser.getId() : "Guest", commentId, commentForm.getUserId());

            redirectAttributes.addFlashAttribute("errorMessage", "E0025");
            return new ModelAndView("redirect:/top");
        }

        ModelAndView mav = new ModelAndView();

        commentService.deleteComment(commentId);

        log.info("[CommentController] Comment deletion successful - ID: {}", id);

        mav.setViewName("redirect:/top");
        return mav;
    }
}