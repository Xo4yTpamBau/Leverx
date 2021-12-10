package com.sprect.service.gameObject;

import com.sprect.model.entity.GameObject;

import java.util.List;

public interface GameObjectService {
    List<GameObject> add(GameObject gameObject, Long idPage);
}
