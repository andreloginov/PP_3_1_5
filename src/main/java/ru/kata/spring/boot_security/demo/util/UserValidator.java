package ru.kata.spring.boot_security.demo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Objects;
import java.util.Optional;

@Component
public class UserValidator implements Validator {

    private final UserService userService;

    public UserValidator(UserService userService) {
        this.userService = userService;
    }


    @Override
    public boolean supports(Class<?> clazz) {

        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        user.setPassword(user.getPasswordConfirm());

        if (userService.findByName(user.getName()).isPresent()) {
            errors.rejectValue("name", "", "This name is already taken");
        }

    }

    public void validateForUpdateForm(Object target, Errors errors) {
        User user = (User) target;
        User userFromDB = userService.findByName(user.getName()).orElseGet(() -> null);

        if (userFromDB != null && !Objects.equals(user.getId(), userFromDB.getId())) {
            errors.rejectValue("name", "", "This name is already taken");
        }

        String userPassword = user.getPasswordConfirm();
        // if user's pass is more than 4 but not empty and id is not null
        // test on validate new user's password
        if (userPassword.length() < 4 && !userPassword.isBlank() && user.getId() != null) {
            errors.rejectValue("password", "", "This password is very short. Do not enter if you want to keep the current password!");
        } else if (userPassword.length() < 4 && user.getId() == null) {
            //if password too short or empty and id is null
            //if enter new user
            errors.rejectValue("password", "", "This password is too short. Try again!");
            // if user doesn't enter a password and has the id
            // if enter is updating
        } else if (userPassword.isBlank() && user.getId() != null) {
            /*user.setPassword(userPassword);*/
        } else if (userPassword.length() > 3 && user.getId() == null) {
            user.setPassword(userPassword);
        }


    }
}
