package com.sprect.service.comment;

import com.sprect.exception.NotFoundException;
import com.sprect.model.entity.Comment;
import com.sprect.repository.sql.CommentRepository;
import com.sprect.service.jwt.JwtService;
import com.sprect.service.page.PageService;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.sprect.utils.DefaultString.COMMENT_NOT_FOUND;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PageService pageService;
    private final JwtService jwtService;

    public CommentServiceImpl(CommentRepository commentRepository,
                              PageService pageService,
                              JwtService jwtService) {
        this.commentRepository = commentRepository;
        this.pageService = pageService;
        this.jwtService = jwtService;
    }

    @Override
    public void add(Comment comment, Long idPage, String accessToken) {
        Claims body = jwtService.getClaims(accessToken.substring(7)).getBody();
        comment.setIdUser(Long.parseLong(body.getId()));
        comment.setUsernameAuthor(comment.isAnonymous() ? "anonymous" : body.getSubject());
        comment.setPage(pageService.get(idPage));
        commentRepository.save(comment);
    }

    @Override
    public void add(Comment comment, String accessToken) {
        Claims body = jwtService.getClaims(accessToken.substring(7)).getBody();
        comment.setIdUser(Long.parseLong(body.getId()));
        comment.setUsernameAuthor(comment.isAnonymous() ? "anonymous" : body.getSubject());
        commentRepository.save(comment);
    }

    @Override
    public Comment findById(Long idComment) {
        Optional<Comment> comment = commentRepository.findById(idComment);

        if (comment.isEmpty()) {
            throw new NotFoundException(COMMENT_NOT_FOUND);
        }
        return comment.get();
    }


    @Override
    public List<Comment> getNotApproved() {
        return commentRepository.findAllByApproved(false);
    }

    @Override
    public void approved(Long idComment) {
        Comment comment = findById(idComment);
        comment.setApproved(true);
        commentRepository.save(comment);
        pageService.updateRate(comment);
    }

    @Override
    @Transactional
    public void deleteById(Long idComment) {
        commentRepository.deleteById(idComment);
    }

    @Override
    public void updateText(Long idComment, String text) {
        Comment comment = findById(idComment);
        comment.setText(text);
        commentRepository.save(comment);

    }
}
