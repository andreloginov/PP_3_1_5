package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.util.UserValidator;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;


@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final UserValidator userValidator;

    public AdminController(UserService userService, RoleRepository roleRepository, UserValidator userValidator) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.userValidator = userValidator;
    }

    @GetMapping("")
    public String homePage() {
        return "redirect:/admin/employees";
    }

    @GetMapping("/")
    public String needToDelete(Model model) {
        List<User> list = userService.allUsers();
        model.addAttribute("employees", list);
        return "all_users_table";
    }

    @GetMapping("/employees")
    public String showAllEmployees(Model model) {
        model.addAttribute("employee", new User());
        model.addAttribute("employees", userService.allUsers());
        model.addAttribute("roleSet", roleRepository.findAll());

        return "employee-list";
    }

    @GetMapping("/employee-create")
    public String createEmployeeForm(User employee, Model model) {

        model.addAttribute("employee", employee);
        model.addAttribute("roleSet", roleRepository.findAll());




        return "employee-update";
    }
    @GetMapping("employee-delete/{id}")
    public String deleteEmployee(@PathVariable("id") int id) {
        userService.deleteUser(id);

        return "redirect:/admin/employees";
    }

    @GetMapping("/employee-update/{id}")
    public String updateEmployeeForm(@PathVariable("id") int id, Model model) {

        User user = userService.findUserById(id);
        user.setPasswordConfirm(user.getPassword());
        model.addAttribute("employee", user);
        model.addAttribute("roleSet", roleRepository.findAll());

        return "employee-update";
    }

    @PostMapping("/employee-update")
    public String updateEmployee(@Valid @ModelAttribute("employee") User employee, BindingResult bindingResult, Model model) {

        userValidator.validate(employee, bindingResult);
        if (bindingResult.hasErrors()) {

            model.addAttribute("roleSet", roleRepository.findAll());
            model.addAttribute("employees", userService.findUserById(1));

            return "all_users_table";
        }

        userService.saveUser(employee);

        return "redirect:/admin/employees";
    }
}
