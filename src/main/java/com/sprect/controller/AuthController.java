package com.sprect.controller;

import com.sprect.model.entity.User;
import com.sprect.service.file.FileService;
import com.sprect.service.jwt.JwtService;
import com.sprect.service.mail.MailService;
import com.sprect.service.tryAuth.TryAuthService;
import com.sprect.service.user.UserService;
import com.sprect.utils.Validator;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.sprect.utils.DefaultString.*;

@Validated
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final MailService mailService;
    private final Validator validator;
    private final TryAuthService tryAuthService;
    private final FileService fileService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserService userService,
                          MailService mailService,
                          Validator validator,
                          TryAuthService tryAuthService,
                          FileService fileService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.mailService = mailService;
        this.validator = validator;
        this.tryAuthService = tryAuthService;
        this.fileService = fileService;
    }


    @PostMapping("/signUp")
    public ResponseEntity<?> registration(@Valid @RequestBody User user) {
        User saveUser = userService.saveUser(user);
        mailService.sendActivationCode(user.getEmail(), "Confirmation email");
        return new ResponseEntity<>(saveUser, HttpStatus.CREATED);
    }


    @PostMapping("/signIn")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        body.get("username"),
                        body.get("password")));

        Map<String, Object> response = jwtService.createTokens(body.get("username"), List.of("access", "refresh"));

        User user = (User) response.get("user");

        if (user.isAvatar()) {
            user.setUrlAvatar(fileService.getUrlForDownloadAvatar(user.getIdUser().toString()));
        }

        tryAuthService.deleteById(user.getIdUser());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/confirmationEmail/{token}")
    public ResponseEntity<?> confirmationEmail(@PathVariable String token) {
        try {
            String username = jwtService.getClaims(token).getBody().getSubject();

            userService.confirmationEmail(username);

            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create("https://hd.kinopoisk.ru/film/42d5ba8f195451fda78fe0ce899a964a?from_block=kp-button-online"));
            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        } catch (ExpiredJwtException e) {
            throw new JwtException(CONFIRM_EXPIRED);
        } catch (JwtException e) {
            throw new JwtException(CONFIRM_INVALID);
        }
    }

    @PostMapping("/sendEmailForResetPassword")
    public ResponseEntity<?> sendEmailForResetPassword(@RequestBody Map<String, String> body) {
        if (!userService.isEmailExist(body.get("email"))) {
            throw new UsernameNotFoundException(USER_NOT_FOUND);
        }

        mailService.sendEmailForChangePassword(body.get("email"), "Reset password");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/resendEmailForConfirm")
    public ResponseEntity<?> resendEmailForConfirm(@RequestBody Map<String, String> body) {
        if (!userService.isEmailExist(body.get("email"))) {
            throw new UsernameNotFoundException(USER_NOT_FOUND);
        }

        mailService.sendActivationCode(body.get("email"), "Confirmation email");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/updateTokens")
    public ResponseEntity<?> updateTokens(@RequestBody Map<String, String> body) {
        Map<String, Object> newTokens = jwtService.getNewTokens(body.get("accessToken"),
                body.get("refreshToken"));
        return new ResponseEntity<>(newTokens, HttpStatus.OK);
    }

    @PostMapping("/isExistEmail")
    public ResponseEntity<?> isExistEmail(@RequestBody Map<String, String> body) {
        validator.regExpEmail(body.get("email"));
        validator.existEmail(body.get("email"));
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping("/isExistUsername")
    public ResponseEntity<?> isExistUsername(@RequestBody Map<String, String> body) {
        validator.regExpUsername(body.get("username"));
        validator.existUsername(body.get("username"));
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
