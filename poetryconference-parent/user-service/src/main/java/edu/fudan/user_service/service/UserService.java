package edu.fudan.user_service.service;

import edu.fudan.user_service.entity.User;
import edu.fudan.user_service.vo.UserLoginVo;

public interface UserService {
    User register(User user);
    UserLoginVo checkLogin(String username, String password);

    User getUserInfo(Long userId); // getUserInfo
}