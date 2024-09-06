package com.example.electricitybill.service;

import com.example.electricitybill.model.User;
import com.example.electricitybill.repository.User.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User updateUserUsingQueryDSL(String username, User updatedUser) {
        return userRepository.updateUserUsingQueryDSL(username, updatedUser);
    }

    @Transactional
    public boolean deleteUserUsingQueryDSL(int username) {
        return userRepository.deleteUserUsingQueryDSL(username);
    }

    public List<User> getUserByUsernameUsingQueryDSL(String username) {
        return userRepository.getUserByUsernameUsingQueryDSL(username);
    }

    public List<User> getUserByUsernameAndDateUsingQueryDSL(String username, Date registrationDate) {
        return userRepository.getUserByUsernameAndDateUsingQueryDSL(username, registrationDate);
    }

    public User findUserByPhoneNumberUsingQueryDSL(String phoneNumber) {
        return userRepository.findUserByPhoneNumberUsingQueryDSL(phoneNumber);
    }



    public List<User> getAllUsersUsingQueryDSL() {
        return userRepository.getAllUsersUsingQueryDSL();
    }


}