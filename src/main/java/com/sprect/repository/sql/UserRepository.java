package com.sprect.repository.sql;

import com.sprect.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String username);

    User findUserByEmail(String email);

    void deleteUserByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
