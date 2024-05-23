package edu.fudan.poetryconference.service;

import edu.fudan.poetryconference.model.User;
import edu.fudan.poetryconference.vo.UserLoginVo;
import org.springframework.http.ResponseEntity;

public interface UserService {
    User register(User user);
    UserLoginVo checkLogin(String username, String password);

    User getUserInfo(Long userId); // getUserInfo
}
