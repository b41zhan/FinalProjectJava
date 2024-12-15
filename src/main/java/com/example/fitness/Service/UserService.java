package com.example.fitness.Service;


import com.example.fitness.Entity.User;

import java.util.List;

public interface UserService {
    User findByEmail(String email);
    User findByUsername(String username);

    User saveUser(User user);

    User findUserByEmail(String email);


    List<User> findAll();
}
