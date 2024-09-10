package com.example.electricitybill.repository.User;

import com.example.electricitybill.model.User;

import java.util.Date;
import java.util.List;

public interface UserRepositoryCustom {
    User updateUserUsingQueryDSL(String username, User updatedUser);
    boolean deleteUserUsingQueryDSL(int username);
    List<User> getUserByUsernameUsingQueryDSL(String username);
    List<User> getUserByUsernameAndDateUsingQueryDSL(String username, Date registrationDate);

    User findUserByPhoneNumberUsingQueryDSL(int phoneNumber);

    List<User> getAllUsersUsingQueryDSL();
}