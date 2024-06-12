package edu.fudan.user_service.service.imlp;

import edu.fudan.user_service.entity.User;
import edu.fudan.user_service.repository.UserRepository;
import edu.fudan.user_service.service.UserService;
import edu.fudan.user_service.vo.UserLoginVo;
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
    public UserLoginVo checkLogin(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            UserLoginVo userLoginVo = new UserLoginVo();
            userLoginVo.setUserId(user.getUser_id());
            userLoginVo.setUsername(user.getUsername());
            userLoginVo.setModel(user.getModel());
            return userLoginVo;
        }
        return null; // or throw an exception if preferred
    }

    @Override
    public User getUserInfo(Long userId) {
        User user = userRepository.findById(userId).get();
        return user;
    }
}