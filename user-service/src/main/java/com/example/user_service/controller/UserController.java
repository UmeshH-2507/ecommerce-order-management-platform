package com.example.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user_service.dto.LoginRequest;
import com.example.user_service.dto.LoginResponse;
import com.example.user_service.dto.RegisterRequest;
import com.example.user_service.dto.RegisterResponse;
import com.example.user_service.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
     private final UserService userService;



     
    public UserController(UserService userService) {
        this.userService = userService;
    }

     @PostMapping("/register")
    public RegisterResponse register(
            @RequestBody RegisterRequest request) {

        return userService.register(request);
    }


    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @GetMapping("/profile")
    public String profile() {
    return "User profile accessed successfully";
}

}
