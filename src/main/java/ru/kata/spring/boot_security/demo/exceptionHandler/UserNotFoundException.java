package ru.kata.spring.boot_security.demo.exceptionHandler;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Integer id) {
        super("Could not find user " + id);
    }
}
