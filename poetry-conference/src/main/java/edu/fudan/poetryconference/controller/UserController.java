package edu.fudan.poetryconference.controller;

import edu.fudan.poetryconference.dto.UserContestNumDTO;
import edu.fudan.poetryconference.dto.UserContestResultDTO;
import edu.fudan.poetryconference.dto.UserWinRateDTO;
import edu.fudan.poetryconference.model.User;
import edu.fudan.poetryconference.service.ContestResultService;
import edu.fudan.poetryconference.service.UserService;
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

    @Autowired
    private ContestResultService contestResultService;

    @CrossOrigin
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User registered = userService.register(user);
        return ResponseEntity.ok(registered);
    }

    @CrossOrigin
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
            System.out.println("Login request: " + user.getUsername() + " " + user.getPassword());
        if (userService.checkLogin(user.getUsername(),  user.getPassword())) {
            System.out.println("Login successful");
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @CrossOrigin
    @GetMapping("/personal/contest/results/{userId}")
    public ResponseEntity<List<UserContestResultDTO>> getUserContestResults(@PathVariable Long userId){
        List<UserContestResultDTO> results = contestResultService.getUserContestResults(userId);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

//    @GetMapping("/personal/contest/win/rate/{userId}")
//    public ResponseEntity<List<UserWinRateDTO>> getUserContestWinRate(@PathVariable Integer userId){
//        List<UserWinRateDTO> winRate = contestResultService.getUserContestWinRate(userId);
//        return new ResponseEntity<>(winRate, HttpStatus.OK);
//    }

    @CrossOrigin
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserInfo(@PathVariable Long userId) {
        User user = userService.getUserInfo(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping("/win/rate/total/{userId}")
    public Double getUserWinRateTotal(@PathVariable Long userId) {
        System.out.println("controller: "+userId);
        return contestResultService.getUserWinRateTotal(userId);
    }

    @CrossOrigin
    @GetMapping("/contest/num/{userId}")
    public ResponseEntity<List<Integer>> getUserContestNum(@PathVariable Long userId) {
        List<Integer> userContestNum = contestResultService.getUserContestNum(userId);
        return new ResponseEntity<>(userContestNum, HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping("/contest/win/rate/{userId}")
    public ResponseEntity<List<Double>> getUserContestWinRate(@PathVariable Long userId){
        List<Double> userContestWinRate = contestResultService.getUserContestWinRate(userId);
        return new ResponseEntity<>(userContestWinRate, HttpStatus.OK);
    }

}
