package edu.fudan.poetryconference.service.impl;

import edu.fudan.poetryconference.model.User;
import edu.fudan.poetryconference.repository.UserRepository;
import edu.fudan.poetryconference.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public boolean checkLogin(String username, String password) {
        User user = userRepository.findByUsername(username);
        return user != null && passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public User getUserInfo(Long userId) {
        User user = userRepository.findById(userId).get();
        return user;
    }
}
