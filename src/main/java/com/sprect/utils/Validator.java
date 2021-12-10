package com.sprect.utils;

import com.sprect.exception.RightEditException;
import com.sprect.model.entity.User;
import com.sprect.repository.sql.PageRepository;
import com.sprect.repository.sql.UserRepository;
import com.sprect.service.comment.CommentService;
import com.sprect.service.jwt.JwtService;
import com.sprect.service.page.PageService;
import org.springframework.oxm.ValidationFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Locale;

import static com.sprect.utils.DefaultString.*;
import static org.apache.http.entity.ContentType.*;

@Component
public class Validator {
    private final UserRepository userRepository;
    private final PageService pageService;
    private final CommentService commentService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public Validator(UserRepository userRepository,
                     PageService pageService,
                     CommentService commentService,
                     PasswordEncoder passwordEncoder,
                     JwtService jwtService) {
        this.userRepository = userRepository;
        this.pageService = pageService;
        this.commentService = commentService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }




    public void regExpEmail(String email) {
        if (!email.matches(PATTERN_EMAIL))
            throw new ValidationFailureException(FAILED_VALIDATE_EMAIL);
    }

    public void regExpUsername(String username) {
        String newUsername = username.trim().toLowerCase(Locale.ROOT);
        if (newUsername.length() < 3 || newUsername.length() > 18)
            throw new ValidationFailureException(FAILED_VALIDATE_USERNAME);
    }

    public void regExpPassword(String password) {
        if (!password.matches(PATTERN_PASSWORD))
            throw new ValidationFailureException(FAILED_VALIDATE_PASSWORD);
    }

    public void existEmail(String email) {
        if (userRepository.existsByEmail(email))
            throw new ValidationFailureException(EMAIL_BUSY);

    }

    public void existUsername(String username) {
        if (userRepository.existsByUsername(username))
            throw new ValidationFailureException(USERNAME_BUSY);
    }

    public void checkOldPassword(String username, String oldPassword) {
        User user = userRepository.findUserByUsername(username);
        if (!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new ValidationFailureException(WRONG_OLD_PASSWORD);
    }

    public void checkRightEditPage(String accessToken, Long idPage){
        if (Long.parseLong(jwtService.getClaims(accessToken.substring(7)).getBody().getId()) != pageService.findById(idPage).getIdUser() ) {
            throw new RightEditException("У вас нет права для редактирования этой страницы");
        }
    }

    public void checkRightEditComment(String accessToken, Long idComment){
        if (Long.parseLong(jwtService.getClaims(accessToken.substring(7)).getBody().getId()) != commentService.findById(idComment).getIdUser() ) {
            throw new RightEditException("У вас нет права для редактирования этого комментария");
        }
    }

    public void typeFileAvatar(MultipartFile file) {
        if (!Arrays.asList(
                IMAGE_PNG.getMimeType(),
                IMAGE_BMP.getMimeType(),
                IMAGE_GIF.getMimeType(),
                IMAGE_JPEG.getMimeType()).contains(file.getContentType())) {
            throw new IllegalStateException("FIle uploaded is not an image");
        }
    }
}
