package com.sprect.controller;

import com.sprect.service.file.FileService;
import com.sprect.service.jwt.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class FileController {
    private final JwtService jwtService;
    private final FileService fileService;

    public FileController(JwtService jwtService,
                          FileService fileService) {
        this.jwtService = jwtService;
        this.fileService = fileService;
    }

    @PostMapping("/uploadAvatar")
    public ResponseEntity<?> uploadAvatar(@RequestHeader("Authorization") String token,
                                          @RequestParam("file") MultipartFile file) {
        String id = jwtService.getClaims(token.substring(7)).getBody().get("id").toString();
        fileService.saveAvatar(id, file);
        return new ResponseEntity<>(fileService.getUrlForDownloadAvatar(id), HttpStatus.OK);
    }


    @DeleteMapping("/deleteAvatar")
    public ResponseEntity<?> deleteAvatar(@RequestHeader("Authorization") String token) {
        String id = jwtService.getClaims(token.substring(7)).getBody().get("id").toString();
        fileService.deleteAvatar(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


//    @GetMapping("/download")
//    public ResponseEntity<?> downloadFile(@RequestHeader("Authorization") String token) {
//        String id = jwtService.getBodyToken(token.substring(7)).get("id").toString();
//        return new ResponseEntity<>(todoService.downloadAvatar(id), HttpStatus.OK);
//    }
}