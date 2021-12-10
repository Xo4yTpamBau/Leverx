package com.sprect.service.page;

import com.sprect.exception.NotFoundException;
import com.sprect.model.entity.Comment;
import com.sprect.model.entity.Page;
import com.sprect.repository.sql.PageRepository;
import com.sprect.service.comment.CommentService;
import com.sprect.service.game.GameService;
import com.sprect.service.jwt.JwtService;
import com.sprect.service.user.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sprect.utils.DefaultString.PAGE_NOT_FOUND;

@Service
public class PageServiceImpl implements PageService {
    private final PageRepository pageRepository;
    private final UserService userService;
    private final CommentService commentService;
    private final GameService gameService;
    private final JwtService jwtService;

    public PageServiceImpl(PageRepository pageRepository,
                           UserService userService,
                           @Lazy CommentService commentService,
                           GameService gameService,
                           JwtService jwtService) {
        this.pageRepository = pageRepository;
        this.userService = userService;
        this.commentService = commentService;
        this.gameService = gameService;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public void create(Page page, String accessToken) {
        page.setIdUser(Long.parseLong(jwtService.getClaims(accessToken.substring(7)).getBody().getId()));
        userService.setTraderRole(page.getIdUser());
        gameService.create(page);
        pageRepository.save(page);
    }

    @Override
    @Transactional
    public Page get(Long idPage) {
        Page page = findById(idPage);
        if (!page.isApproved()) {
            throw new NotFoundException(PAGE_NOT_FOUND);
        }
        page.setComments(page.getComments()
                .stream()
                .filter(Comment::isApproved)
                .collect(Collectors.toList()));
        return page;
    }

    public Page findById(Long idPage) {
        Optional<Page> page = pageRepository.findById(idPage);
        if (page.isEmpty()) {
            throw new NotFoundException(PAGE_NOT_FOUND);
        }
        return page.get();
    }

    @Override
    @Transactional
    public void delete(Long idPage) {
        pageRepository.deleteById(idPage);
    }

    @Override
    public List<Page> getNotApproved() {
        return pageRepository.findAllByApproved(false);
    }

    @Override
    public void approved(Long idPage) {
        Page page = findById(idPage);
        page.setApproved(true);
        pageRepository.save(page);

    }

    @Override
    public void updateRate(Comment comment) {
        Page page = comment.getPage();
        page.setRate((page.getRate() * page.getCountComment() + comment.getRating()) / (page.getCountComment() + 1));
        page.setCountComment(page.getCountComment() + 1);
        pageRepository.save(page);
    }

    @Override
    public List<Page> getTop() {
        return pageRepository.findAllByApprovedOrderByRateDesc(true);
    }

    @Override
    public void createMissing(Comment comment, String accessToken) {
        Page page = comment.getPage();
        page.setIdUser(0L);
        gameService.create(page);
        Page save = pageRepository.save(page);
        comment.setPage(save);
        commentService.add(comment, accessToken);
    }

    @Override
    public void updateDescription(Long idPage, String description) {
        Page page = findById(idPage);
        page.setDescription(description);
        pageRepository.save(page);
    }
}
