package com.sprect.service;

import com.sprect.exception.StatusException;
import com.sprect.model.StatusUser;
import com.sprect.model.Role;
import com.sprect.model.entity.User;
import com.sprect.repository.sql.UserRepository;
import com.sprect.service.user.UserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static com.sprect.utils.DefaultString.*;


@SpringBootTest
@RunWith(SpringRunner.class)
class UserServiceTest {
    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void saveUser() {
        User user = new User();
        Role role = Role.USER;
        user.setUsername(" USER ");

        Mockito.doReturn(user).when(userRepository).save(user);
        userService.saveUser(user);

        Assertions.assertEquals(StatusUser.NOT_ACTIVE, user.getStatus());
        Assertions.assertEquals(Role.USER, user.getRole());
        Assertions.assertEquals("user", user.getUsername());

        Mockito.verify(userRepository, Mockito.times(1)).save(user);
        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(user.getPassword());
    }

    @Test
    @SneakyThrows
    void checkBlockedUser() {
        userService.checkBlockedUser(StatusUser.ACTIVE);

        StatusException exception;

        exception = Assertions.assertThrows(
                StatusException.class,
                () -> userService.checkBlockedUser(StatusUser.BLOCKED));
        Assertions.assertEquals(USER_BLOCKED, exception.getMessage());
    }

    @Test
    @SneakyThrows
    void findUserByUEN() {
        Mockito.doReturn(new User()).when(userRepository).findUserByEmail("user@mail.ru");
        userService.findUserByUE("user@mail.ru");
        Mockito.verify(userRepository, Mockito.times(1)).findUserByEmail("user@mail.ru");

        Mockito.doReturn(new User()).when(userRepository).findUserByUsername("user");
        userService.findUserByUE("user");
        Mockito.verify(userRepository, Mockito.times(1)).findUserByUsername("user");
    }

    @Test
    @SneakyThrows
    void failedFindUserByUEN() {
        UsernameNotFoundException exception;

        exception = Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> userService.findUserByUE("user"));
        Assertions.assertEquals(USER_NOT_FOUND, exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findUserByUsername("user");

        exception = Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> userService.findUserByUE("user@mail.com"));
        Assertions.assertEquals(USER_NOT_FOUND, exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findUserByEmail("user@mail.com");
    }

    @Test
    void confirmationEmail() {
        User user = new User();
        Mockito.doReturn(user).when(userRepository).findUserByEmail("user@mail.ru");

        userService.confirmationEmail("user@mail.ru");

        Assertions.assertEquals(StatusUser.ACTIVE, user.getStatus());
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    void resetPassword() {
        User user = new User();
        Mockito.doReturn(user).when(userRepository).findUserByUsername("user");

        userService.resetPassword("user", user.getPassword());

        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(user.getPassword());
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    void editUsername() {
        User user = new User();
        user.setUsername("oldTest");

        Mockito.doReturn(user).when(userRepository).findUserByUsername("oldTest");
        userService.editUsername("oldTest", "newTest");

        Mockito.verify(userRepository, Mockito.times(1)).save(user);
        Assertions.assertEquals("newTest", user.getUsername());
    }
}