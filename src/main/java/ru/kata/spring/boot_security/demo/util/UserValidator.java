package ru.kata.spring.boot_security.demo.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import org.springframework.validation.Validator;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Objects;


@Component
public class UserValidator implements Validator {

    private final UserService userService;
    private final RoleRepository repository;


    public UserValidator(UserService userService, RoleRepository repository) {
        this.userService = userService;
        this.repository = repository;
    }


    @Override
    public boolean supports(Class<?> clazz) {

        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        int minLengthPassword = 4;
        User user = (User) target;
        String userPassword = user.getPasswordConfirm();
        User userFromDB = userService.findByName(user.getName()).orElse(null);

        if (userFromDB != null && !Objects.equals(user.getId(), userFromDB.getId())) {
            errors.rejectValue("name", "", "this name is already taken");
        } else if (user.getName().isBlank()) {
            errors.rejectValue("name", "", "the length of the field must be at least 4 characters");
        }


        // if user's pass is more than 4 but not empty and id is not null
        // test on validate new user's password

        if (userPassword.length() < minLengthPassword && !userPassword.isBlank() && user.getId() != null) {
            errors.rejectValue("password", "", "This password is very short. Do not enter if you want to keep the current password!");
        } else if (userPassword.length() < 4 && user.getId() == null) {
            //if password too short or empty and id is null
            //if enter new user
            errors.rejectValue("password", "", "This password is too short. Try again!");
            // if user doesn't enter a password and has the id
            // if enter is updating
        } else if (userPassword.length() >= minLengthPassword && user.getId() != null) {
            user.setPassword(userPassword);
        } else if (userPassword.length() >= minLengthPassword && user.getId() == null) {
            user.setPassword(userPassword);
        }

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            errors.rejectValue("roles", "", "Please select something");
        }
    }

}
