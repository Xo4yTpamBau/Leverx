package com.sprect.service.gameObject;

import com.sprect.model.entity.GameObject;
import com.sprect.model.entity.Page;
import com.sprect.repository.sql.GameObjectRepository;
import com.sprect.service.page.PageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameObjectServiceImpl implements GameObjectService {
    private final GameObjectRepository gameObjectRepository;
    private final PageService pageService;

    public GameObjectServiceImpl(GameObjectRepository gameObjectRepository,
                                 PageService pageService) {
        this.gameObjectRepository = gameObjectRepository;
        this.pageService = pageService;
    }

    @Override
    public List<GameObject> add(GameObject gameObject, Long idPage) {
        Page page = pageService.findById(idPage);
        gameObject.setPage(page);
        gameObjectRepository.save(gameObject);
        return gameObjectRepository.findAllByPage(page);
    }
}
