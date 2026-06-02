package com.example.ariga_seiya_forum.service;

import com.example.ariga_seiya_forum.controller.form.CommentForm;
import com.example.ariga_seiya_forum.controller.form.MessageForm;
import com.example.ariga_seiya_forum.entity.Comment;
import com.example.ariga_seiya_forum.entity.Message;
import com.example.ariga_seiya_forum.repository.CommentRepository;
import com.example.ariga_seiya_forum.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CommentService {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    MessageRepository messageRepository;

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
            commentForm.setUserId(comment.getUserId());

            if (comment.getUser() != null) {
                commentForm.setName(comment.getUser().getName());       // ユーザー名
                commentForm.setAccount(comment.getUser().getAccount()); // アカウント名
            }

            commentList.add(commentForm);
        }
        return commentList;
    }

    /*
     * 保存処理（新規登録・更新）
     */
    @Transactional
    public void saveComment(CommentForm reqComment) {

        Message message = messageRepository.findById(reqComment.getMessageId()).orElse(null);

        if (message == null) {
            log.warn("[CommentService] Message ID: {} not found.", reqComment.getMessageId());
            throw new IllegalArgumentException("Message ID " + reqComment.getMessageId() + " not found.");
        }

        // IDが0なら新規(Create)、それ以外なら更新(Update)としてログを出し分ける
        if (reqComment.getId() == null) {
            log.info("[CommentService] Creating new comment - Message ID: {}, Content: {}",
                    reqComment.getMessageId(), reqComment.getContent());
        } else {
            log.info("[CommentService] Updating existing comment - CommentID: {}, Content: {}",
                    reqComment.getId(), reqComment.getContent());
        }

        Comment saveComment = setCommentEntity(reqComment);
        commentRepository.save(saveComment);

        log.info("[CommentService] Updated lastCommentedAt for Message ID: {}", message.getId());

        // データベースへの保存命令が完了したことを記録
        log.info("[CommentService] Save operation completed successfully.");
    }

    /*
     * FormからEntityへの変換（保存準備）
     */
    private Comment setCommentEntity(CommentForm reqComment) {

        log.info("[CommentService] Converting Form to Entity - Message ID: {}", reqComment.getMessageId());

        Comment comment = new Comment();
        comment.setId(reqComment.getId());
        comment.setContent(reqComment.getContent());
        comment.setMessageId(reqComment.getMessageId());
        comment.setUserId(reqComment.getUserId());

        return comment;
    }

    /*
     * 返信削除処理
     */
    @Transactional
    public void deleteComment(int id) {
        log.warn("[CommentService] Executing delete - CommentID: {}", id);

        commentRepository.deleteById(id);

        log.info("[CommentService] Delete completed successfully - CommentID: {}", id);
    }
}
