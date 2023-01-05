package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.entity.User;

import java.util.List;

public interface UserService {
    public List<User> getAllUsers();
    public void saveUser(User user);
    public User getUserById(int id);
    public void deleteUser(int id);
}
