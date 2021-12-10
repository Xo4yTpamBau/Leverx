package com.sprect.service.game;

import com.sprect.model.entity.Game;
import com.sprect.model.entity.Page;
import com.sprect.repository.sql.GameRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;

    public GameServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public void create(Page page) {
        List<Game> games = page.getGames();
        page.setGames(new ArrayList<>());
        for (Game game : games) {
            if (!exist(game)) {
                game.setName(game.getName().toLowerCase(Locale.ROOT));
                Game save = gameRepository.save(game);
                page.getGames().add(save);
            } else {
                page.getGames().add(gameRepository.findByName(game.getName()));
            }
        }
    }

    private boolean exist(Game game) {
        return gameRepository.existsByName(game.getName().toLowerCase(Locale.ROOT));
    }

}
