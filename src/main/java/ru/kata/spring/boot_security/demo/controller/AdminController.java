package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;


@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String homePage() {
        return "redirect:/admin/employees";
    }

    @GetMapping("/employees")
    public String showAllEmployees(Model model) {
        model.addAttribute("employees", userService.allUsers());

        return "employee-list";
    }

    @GetMapping("/employee-create")
    public String createEmployeeForm(User employee, Model model) {
        model.addAttribute("employee", employee);

            model.addAttribute("sms1", "Поле не может быть пустым");


        return "employee-update";
    }
    @GetMapping("employee-delete/{id}")
    public String deleteEmployee(@PathVariable("id") int id) {
        userService.deleteUser(id);

        return "redirect:/admin/employees";
    }

    @GetMapping("/employee-update/{id}")
    public String updateEmployeeForm(@PathVariable("id") int id, Model model) {
        model.addAttribute("employee", userService.findUserById(id));

        return "employee-update";
    }

    @PostMapping("/employee-update")
    public String updateEmployee(@Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("employee", user);
            return "employee-update";
        }
        userService.saveUser(user);

        return "redirect:/admin/employees";
    }
}
