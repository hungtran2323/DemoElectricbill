package com.example.electricitybill.repository.User;

import com.example.electricitybill.model.User;

import java.util.Date;
import java.util.List;

public interface UserRepositoryCustom {
    User updateUserUsingQueryDSL(String username, User updatedUser);
    boolean deleteUserUsingQueryDSL(String username);
    List<User> getUserByUsernameUsingQueryDSL(String username);
    List<User> getUserByUsernameAndDateUsingQueryDSL(String username, Date registrationDate);

    User findUserByPhoneNumberUsingQueryDSL(String phoneNumber);

    List<User> getAllUsersUsingQueryDSL();
}