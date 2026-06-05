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

        log.info("[CommentService] Creating new comment - Message ID: {}, Content: {}",
                reqComment.getMessageId(), reqComment.getContent());


        Comment saveComment = setCommentEntity(reqComment);
        commentRepository.save(saveComment);

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

    /*
     * 投稿を1件取得
     */
    public CommentForm findCommentById(Integer id) {

        log.info("[CommentService] Attempting to find comment by ID: {}", id);

        List<Comment> results = new ArrayList<>();
        Comment comment = commentRepository.findById(id).orElse(null);

        if (comment == null) {
            log.warn("[CommentService] Comment not found - CommentID: {}", id);
            return null;
        }

        log.info("[CommentService] Comment successfully retrieved from database. Title: {}", comment.getId());

        results.add(comment);
        List<CommentForm> commentList = setCommentForm(results);

        return commentList.get(0);
    }
}
