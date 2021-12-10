package com.sprect.repository.sql;

import com.sprect.model.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
    boolean existsByName(String name);
    Game findByName(String name);
}
