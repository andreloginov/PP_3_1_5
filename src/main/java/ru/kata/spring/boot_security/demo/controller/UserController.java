package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.UserService;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String userInfo(Authentication authentication, Model model) {
        User user = (User) userService.loadUserByUsername(authentication.getName());
        model.addAttribute("employees", user);

        System.out.println("");
        System.out.println("Handled by controller");
        System.out.println("");
        return "user";
    }

}
