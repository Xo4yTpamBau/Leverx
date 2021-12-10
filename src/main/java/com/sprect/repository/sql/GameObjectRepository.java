package com.sprect.repository.sql;

import com.sprect.model.entity.GameObject;
import com.sprect.model.entity.Page;
import com.sprect.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameObjectRepository extends JpaRepository<GameObject, Long> {
    List<GameObject> findAllByPage(Page page);
}
