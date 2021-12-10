package com.sprect.controller;

import com.sprect.model.entity.Comment;
import com.sprect.model.entity.GameObject;
import com.sprect.model.entity.Page;
import com.sprect.service.gameObject.GameObjectService;
import com.sprect.service.page.PageService;
import com.sprect.utils.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/page")
public class PageController {
    private final PageService pageService;
    private final GameObjectService gameObjectService;
    private final Validator validator;

    public PageController(PageService pageService,
                          GameObjectService gameObjectService,
                          Validator validator) {
        this.pageService = pageService;
        this.gameObjectService = gameObjectService;
        this.validator = validator;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody Page page,
                                    @RequestHeader("Authorization") String accessToken) {
        pageService.create(page, accessToken);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/createMissing")
    public ResponseEntity<?> createMissing(@Valid @RequestBody Comment comment,
                                           @RequestHeader("Authorization") String accessToken) {
        pageService.createMissing(comment, accessToken);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{idPage}")
    public ResponseEntity<?> get(@PathVariable Long idPage) {
        return new ResponseEntity<>(pageService.get(idPage), HttpStatus.OK);
    }

    @GetMapping("/top")
    public ResponseEntity<?> getTop() {
        return new ResponseEntity<>(pageService.getTop(), HttpStatus.OK);
    }

    @DeleteMapping("/{idPage}")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String accessToken,
                                    @PathVariable Long idPage) {
        validator.checkRightEditPage(accessToken, idPage);
        pageService.delete(idPage);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{idPage}/addGameObjects")
    public ResponseEntity<?> addGameObject(@Valid @RequestBody GameObject gameObject,
                                           @PathVariable Long idPage) {
        return new ResponseEntity<>(gameObjectService.add(gameObject, idPage), HttpStatus.CREATED);
    }

    @PatchMapping("/edit/{idPage}/description")
    public ResponseEntity<?> editDescription(@RequestHeader("Authorization") String accessToken,
                                             @RequestBody Map<String, String> body,
                                             @PathVariable Long idPage) {
        validator.checkRightEditPage(accessToken, idPage);
        pageService.updateDescription(idPage, body.get("description"));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/notAppr")
    @PreAuthorize("hasAuthority('MODERATOR')")
    public ResponseEntity<?> getNotApproved() {
        return new ResponseEntity<>(pageService.getNotApproved(), HttpStatus.OK);
    }

    @PatchMapping("/approved/{idPage}")
    @PreAuthorize("hasAuthority('MODERATOR')")
    @Transactional
    public ResponseEntity<?> approved(@PathVariable Long idPage) {
        pageService.approved(idPage);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
