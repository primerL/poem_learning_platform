package edu.fudan.poetryconference.service;

import edu.fudan.poetryconference.model.User;

public interface UserService {
    User register(User user);
    boolean checkLogin(String username, String password);
}
