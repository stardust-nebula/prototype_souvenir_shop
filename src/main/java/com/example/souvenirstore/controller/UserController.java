package com.example.souvenirstore.controller;


import com.example.souvenirstore.dto.AuthenticationRequestDto;
import com.example.souvenirstore.entity.Token;
import com.example.souvenirstore.entity.User;
import com.example.souvenirstore.exception.ExceptionHandler;
import com.example.souvenirstore.service.TokenService;
import com.example.souvenirstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/registration")
    public ResponseEntity<Object> registration(@RequestBody User user) {
        try {
            userService.save(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (ExceptionHandler e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/authorize")
    public ResponseEntity<Object> login(@RequestBody AuthenticationRequestDto authReqDto) {

        try {
            String username = authReqDto.getUsername();
            Optional<User> user = userService.login(username, authReqDto.getPassword());
            long userId = user.get().getId();
            boolean isUserHasActiveToken = tokenService.isUserHasActiveToken(userId);

            Token token;
            if (!isUserHasActiveToken){
                token = tokenService.save(user.get());
            }else {
                token = tokenService.getActiveTokenByUserId(userId);
            }

            Map<Object, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("token", token);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ExceptionHandler e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity logout(@RequestHeader(name = "X-Token") UUID xToken) {
        if (!tokenService.isUserTokenExist(xToken)) {
            return ResponseEntity.badRequest().body("Not authorized user");
        }
        Token token = tokenService.getTokenByUuid(xToken);
        tokenService.delete(token);
        return ResponseEntity.ok("The user is logout");
    }

}
