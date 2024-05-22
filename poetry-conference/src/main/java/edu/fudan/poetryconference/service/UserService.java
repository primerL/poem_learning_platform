package edu.fudan.poetryconference.service;

import edu.fudan.poetryconference.model.User;
import org.springframework.http.ResponseEntity;

public interface UserService {
    User register(User user);
    boolean checkLogin(String username, String password);

    User getUserInfo(Long userId); // getUserInfo
}
