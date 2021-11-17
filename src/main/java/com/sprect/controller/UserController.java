package com.sprect.controller;

import com.sprect.model.entity.User;
import com.sprect.service.file.FileService;
import com.sprect.service.jwt.JwtService;
import com.sprect.service.user.UserService;
import com.sprect.utils.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    private final Validator validator;
    private final FileService fileService;

    public UserController(UserService userService,
                          JwtService jwtService,
                          Validator validator,
                          FileService fileService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.validator = validator;
        this.fileService = fileService;
    }


    @GetMapping("/get")
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String token) {
        String username = jwtService.getClaims(token.substring(7)).getBody().getSubject();

        User user = userService.findUserByUE(username);

        Map<String, Object> response = new HashMap<>();
        response.put("user", user);

        if (user.isAvatar()) {
            user.setUrlAvatar(fileService.getUrlForDownloadAvatar(user.getIdUser().toString()));
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/resetPasswordThroughEmail")
    public ResponseEntity<?> resetPasswordThroughEmail(@RequestHeader("Authorization") String token,
                                                       @RequestBody Map<String, String> body) {
        validator.regExpPassword(body.get("password"));
        String username = jwtService.getClaims(token.substring(7)).getBody().getSubject();

        userService.resetPassword(username, body.get("password"));
        jwtService.addBlackList(token.substring(7));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/edit/password")
    public ResponseEntity<?> resetPassword(@RequestHeader("Authorization") String token,
                                           @RequestBody Map<String, String> body) {
        validator.regExpPassword(body.get("newPassword"));

        String username = jwtService.getClaims(token.substring(7)).getBody().getSubject();

        validator.checkOldPassword(username, body.get("oldPassword"));

        userService.resetPassword(username, body.get("newPassword"));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/edit/username")
    public ResponseEntity<?> editUsername(@RequestHeader("Authorization") String token,
                                          @RequestBody Map<String, String> body) {
        validator.regExpUsername(body.get("newUsername"));

        String oldUsername = jwtService.getClaims(token.substring(7)).getBody().getSubject();

        User newUser = userService.editUsername(oldUsername, body.get("newUsername"));
        Map<String, Object> response = jwtService.createTokens(newUser.getUsername(), List.of("access", "refresh"));

        jwtService.addBlackList(token.substring(7));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token) {
        Object id = jwtService.getClaims(token.substring(7)).getBody().get("id");
        userService.delete(id.toString());
        jwtService.addBlackList(token.substring(7));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String accessToken) {
        jwtService.addBlackList(accessToken.substring(7));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
