package com.example.electricitybill.repository.User;

import com.example.electricitybill.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>, UserRepositoryCustom {
    List<User> findByUsername(String username);
    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByEmail(String email);
}