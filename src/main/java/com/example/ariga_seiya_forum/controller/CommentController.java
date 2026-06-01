package com.example.ariga_seiya_forum.controller;

import com.example.ariga_seiya_forum.controller.form.CommentForm;
import com.example.ariga_seiya_forum.controller.form.MessageForm;
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

import java.util.List;

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
    public ModelAndView deleteComment(@PathVariable int id) {
        log.info("[CommentController] Received request to delete comment - ID: {}", id);

        ModelAndView mav = new ModelAndView();

        commentService.deleteComment(id);

        log.info("[CommentController] Comment deletion successful - ID: {}", id);

        mav.setViewName("redirect:/top");
        return mav;
    }
}
