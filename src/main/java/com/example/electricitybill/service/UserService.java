package com.example.electricitybill.service;

import com.example.electricitybill.model.User;

import java.util.List;

public interface UserService {
    User getUserById(Integer id);
    List<User> getAllUsers();
    User saveUser(User user);
    void deleteUser(User user);
}