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
    @Autowired
    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Autowired
    public PasswordEncoder passwordEncoder;

    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public void deleteUserById(Long id) {
        userRepo.deleteById(id);
    }

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

    public List<User> findAll() {
        return userRepo.findAll(); // Используем метод findAll из UserRepo
    }

}
