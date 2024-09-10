package com.example.electricitybill.controller;

import com.example.electricitybill.model.User;
import com.example.electricitybill.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@RequestHeader("Authorization") String token, @RequestBody User user) {
        return userService.addUser(token, user);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(token, id, user);
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token, @PathVariable int id) {
        return userService.deleteUser(token, id);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @GetMapping("/find/all")
    public ResponseEntity<?> getAllUsers() {
        return userService.getAllUsers();
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        return userService.login(email, password);
    }
}