package com.example.fitness.Service.Implementation;

import com.example.fitness.Entity.User;
import com.example.fitness.Repository.UserRepo;
import com.example.fitness.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepo.deleteById(id);
    }

    @Override
    public User saveUserWithPhoto(User user) {
        return userRepo.save(user);
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return userRepo.findAll();
    }

    @Override
    public void savePasswordResetCode(User user, String code) {
        user.setResetCode(code);
        userRepo.save(user);
    }

    @Override
    public boolean isResetCodeValid(User user, String code) {
        return code.equals(user.getResetCode());
    }

    @Override
    public void updatePassword(User user, String newPassword) {
        System.out.println("Updating password for user: " + user.getEmail());
        String encodedPassword = passwordEncoder.encode(newPassword);
        System.out.println("Encoded password: " + encodedPassword);
        user.setPassword(encodedPassword);
        userRepo.save(user);
    }
}
