package com.sprect.service.comment;

import com.sprect.model.entity.Comment;

import java.util.List;

public interface CommentService {

    void add(Comment comment,Long idPage, String accessToken);

    void add(Comment comment, String accessToken);

    Comment findById(Long idComment);

    List<Comment> getNotApproved();

    void approved(Long idComment);

    void deleteById(Long idComment);

    void updateText(Long idComment, String text);
}
