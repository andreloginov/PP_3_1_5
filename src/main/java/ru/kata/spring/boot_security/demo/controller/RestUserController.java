package ru.kata.spring.boot_security.demo.controller;

import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
public class RestUserController {

    private final UserService userService;
    public RestUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    List<User> getAllUsers() {
        return userService.allUsers();
    }

    @PostMapping("/users")
    User addNewUser(@RequestBody User user) {
        userService.saveUser(user);
        return user;
    }

    // Single item

    @GetMapping("/users/{id}")
    User getSingleUser(@PathVariable Integer id) {
        return userService.findUserById(id);
    }

    @PutMapping("/users/{id}")
    User updateExistingUser(@RequestBody User userUpdate, @PathVariable Integer id) {
        userService.saveUser(userUpdate);
        return userService.findUserById(id);
    }

    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
    }



}
