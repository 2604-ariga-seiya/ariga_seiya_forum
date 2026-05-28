package com.example.ariga_seiya_forum.service;

import com.example.ariga_seiya_forum.controller.form.CommentForm;
import com.example.ariga_seiya_forum.controller.form.MessageForm;
import com.example.ariga_seiya_forum.entity.Comment;
import com.example.ariga_seiya_forum.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CommentService {

    @Autowired
    CommentRepository commentRepository;

    public List<CommentForm> findAllComments(List<MessageForm> messageList){

        List<CommentForm> commentList = null;

        if(messageList == null || messageList.isEmpty()){
            return commentList;
        }

        List<Comment>results = commentRepository.findAll();

        return setCommentForm(results);
    }

    private List<CommentForm> setCommentForm(List<Comment> results) {
        log.info("[CommentService] Converting Entity to Form - Count: {} items", results.size());
        List<CommentForm> commentList = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            CommentForm commentForm = new CommentForm();
            Comment comment = results.get(i);

            commentForm.setId(comment.getId());
            commentForm.setContent(comment.getContent());
            commentForm.setMessageId(comment.getMessageId());
            commentForm.setCreatedDate(comment.getCreatedDate());
            commentForm.setUpdatedDate(comment.getUpdatedDate());

            // 💡【追加】JPAで結合したUserから、ユーザー名とアカウントを抜いてFormに設定する
            if (comment.getUser() != null) {
                commentForm.setName(comment.getUser().getName());       // ユーザー名
                commentForm.setAccount(comment.getUser().getAccount()); // アカウント名
            }

            commentList.add(commentForm);
        }
        return commentList;
    }
}
