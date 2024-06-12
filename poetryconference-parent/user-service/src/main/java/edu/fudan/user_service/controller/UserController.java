package edu.fudan.user_service.controller;

import edu.fudan.user_service.entity.User;
import edu.fudan.user_service.service.UserService;
import edu.fudan.user_service.vo.UserLoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @CrossOrigin
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User registered = userService.register(user);
        return ResponseEntity.ok(registered);
    }

    @CrossOrigin
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        UserLoginVo userLoginVo = userService.checkLogin(user.getUsername(), user.getPassword());
        if (userLoginVo != null) {
            return ResponseEntity.ok(userLoginVo);
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

}
