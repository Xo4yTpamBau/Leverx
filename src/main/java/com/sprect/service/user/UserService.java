package com.sprect.service.user;

import com.sprect.model.StatusUser;
import com.sprect.model.entity.User;
import javassist.NotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

public interface UserService extends org.springframework.security.core.userdetails.UserDetailsService {
    User saveUser(User user);

    void checkBlockedUser(StatusUser status);

    User findUserByUE(String username);

    void confirmationEmail(String username);

    void resetPassword(String username, String password);

    @Transactional
    void delete(String id);

    void saveAvatar(String id);

    User editUsername(String oldUsername, String newUsername);


    boolean isEmailExist(String email);

    void deleteAvatar(String id);

    @Override
    UserDetails loadUserByUsername(String username);

    UserDetails loadUserByUsername(String username, String JWT);

    void setTraderRole(Long idUser);
}
