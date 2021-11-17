package com.sprect.service.user;

import com.sprect.exception.RegistrationException;
import com.sprect.exception.StatusException;
import com.sprect.model.Role;
import com.sprect.model.StatusUser;
import com.sprect.model.entity.User;
import com.sprect.repository.sql.UserRepository;
import com.sprect.service.file.FileService;
import com.sprect.service.tryAuth.TryAuthService;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.oxm.ValidationFailureException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

import static com.sprect.utils.DefaultString.*;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;
    private final TryAuthService tryAuthService;


    public UserServiceImpl(UserRepository userRepository,
                           @Lazy PasswordEncoder passwordEncoder,
                           @Lazy FileService fileService,
                           @Lazy TryAuthService tryAuthService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileService = fileService;
        this.tryAuthService = tryAuthService;
    }

    @Override
    public User saveUser(User user) {
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setUsername(user.getUsername().toLowerCase(Locale.ROOT).trim());
        user.setPassword(encodePassword);
        setRoleUser(user);
        user.setStatus(StatusUser.NOT_ACTIVE);

        User save;
        try {
            save = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new RegistrationException(REGISTRATION_EXCEPTION);
        }
        return save;

    }

    private void setRoleUser(User user) {
        user.setRole(Role.USER);
    }

    @Override
    public void checkBlockedUser(StatusUser status) {
        if (status.equals(StatusUser.BLOCKED)) {
            throw new StatusException(USER_BLOCKED);
        }
    }

    @Override
    public User findUserByUE(String username) {
        User user = username.matches(PATTERN_EMAIL) ?
                userRepository.findUserByEmail(username) : userRepository.findUserByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(USER_NOT_FOUND);
        }

        return user;
    }

    @Override
    public void confirmationEmail(String username) {
        User user = findUserByUE(username);
        user.setStatus(StatusUser.ACTIVE);
        userRepository.save(user);
    }

    @Override
    public void resetPassword(String username, String password) {
        String encodePassword = passwordEncoder.encode(password);
        User user = userRepository.findUserByUsername(username);
        user.setPassword(encodePassword);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(String id) {
        fileService.deleteAvatar(id);
        userRepository.deleteById(Long.parseLong(id));
    }

    @Override
    public void saveAvatar(String id) {
        Optional<User> byId = userRepository.findById(Long.parseLong(id));
        if (byId.isPresent()) {
            User user = byId.get();
            user.setAvatar(true);
            userRepository.save(user);
        }
    }

    @Override
    public User editUsername(String oldUsername, String newUsername) {
        User user = userRepository.findUserByUsername(oldUsername);
        user.setUsername(newUsername);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ValidationFailureException(USERNAME_BUSY);
        }
        return user;
    }

    @Override
    public boolean isEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void deleteAvatar(String id) {
        Optional<User> byId = userRepository.findById(Long.parseLong(id));
        if (byId.isPresent()) {
            User user = byId.get();
            user.setAvatar(false);
            userRepository.save(user);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findUserByUE(username);
        tryAuthService.checkTryAuth(user.getIdUser());
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username, String JWT) throws UsernameNotFoundException {
        User user = findUserByUE(username);
        checkBlockedUser(user.getStatus());
        return user;
    }
}