package com.example.fitness.Service;


import com.example.fitness.Entity.User;

import java.util.List;

public interface UserService {
    User findByEmail(String email);
    User findByUsername(String username);
    User saveUser(User user);
    User findUserByEmail(String email);
    List<User> findAll();
    void deleteUserById(Long id);
    User saveUserWithPhoto(User user);
    void savePasswordResetCode(User user, String code);
    boolean isResetCodeValid(User user, String code);
    void updatePassword(User user, String newPassword);
}
