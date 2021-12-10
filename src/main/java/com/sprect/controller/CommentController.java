package com.sprect.controller;

import com.sprect.model.entity.Comment;
import com.sprect.service.comment.CommentService;
import com.sprect.utils.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/comment")
@Validated
public class CommentController {
    private final CommentService commentService;
    private final Validator validator;

    public CommentController(CommentService commentService,
                             Validator validator) {
        this.commentService = commentService;
        this.validator = validator;
    }

    @PostMapping("/add/{idPage}")
    public ResponseEntity<?> add(@Valid @RequestBody Comment comment,
                                 @PathVariable Long idPage,
                                 @RequestHeader("Authorization") String accessToken) {
        commentService.add(comment, idPage, accessToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/edit/{idComment}/description")
    public ResponseEntity<?> editDescription(@RequestHeader("Authorization") String accessToken,
                                             @RequestBody Map<String, String> body,
                                             @PathVariable Long idComment) {
        validator.checkRightEditComment(accessToken, idComment);
        commentService.updateText(idComment, body.get("description"));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{idComment}")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String accessToken,
                                    @PathVariable Long idComment) {
        validator.checkRightEditComment(accessToken, idComment);
        commentService.deleteById(idComment);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/notAppr")
    @PreAuthorize("hasAuthority('MODERATOR')")
    public ResponseEntity<?> getNotApproved() {
        return new ResponseEntity<>(commentService.getNotApproved(), HttpStatus.OK);
    }

    @PatchMapping("/approved/{idComment}")
    @PreAuthorize("hasAuthority('MODERATOR')")
    @Transactional
    public ResponseEntity<?> approved(@PathVariable Long idComment) {
        commentService.approved(idComment);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
