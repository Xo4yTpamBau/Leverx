package com.sprect.service.page;

import com.sprect.model.entity.Comment;
import com.sprect.model.entity.Page;

import java.util.List;

public interface PageService {
    void create(Page page, String accessToken);

    Page get(Long idPage);

    Page findById(Long idPage);

    void delete(Long idPage);

    List<Page> getNotApproved();

    void approved(Long idPage);

    void updateRate(Comment comment);

    List<Page> getTop();

    void createMissing(Comment comment, String accessToken);

    void updateDescription(Long idPage, String description);
}
