package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;


@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    public AdminController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
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
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(new Role(1, "ROLE_USER"));
        roleSet.add(new Role(2, "ROLE_ADMIN"));
        model.addAttribute("roleSet", roleSet);



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

        return "employee-update";
    }

    @PostMapping("/employee-update")
    public String updateEmployee(@Valid @ModelAttribute("employee") User employee, BindingResult bindingResult) {

        // for new user
        if (employee.getId() == null) {
            if (bindingResult.hasErrors()) {
                employee.setPassword(null);

                return "employee-update";
            }
        } else {
            // for update
            String userPassword = employee.getPasswordConfirm();

            if (userPassword.length() < 4 && !userPassword.isBlank()) {
                bindingResult.addError(
                        new FieldError("employee",
                                "passwordConfirm",
                                "You've entered a very short pass. ")
                );
            }

            if (bindingResult.hasErrors()) {

                return "employee-update";
            }

            if (!employee.getPasswordConfirm().isBlank() && employee.getPasswordConfirm().length() > 3) {
                employee.setPassword(employee.getPasswordConfirm());
            }
        }


        userService.saveUser(employee);

        return "redirect:/admin/employees";
    }
}
