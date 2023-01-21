package ru.kata.spring.boot_security.demo.controller;

import jdk.jfr.ContentType;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

// should add a handle of EmployeeNotFoundException

@RestController
@RequestMapping("/api")
public class RestUserController {

    private final UserModelAssembler assembler;
    private final UserService userService;

    public RestUserController(UserModelAssembler assembler, UserService userService) {
        this.assembler = assembler;
        this.userService = userService;
    }

    @GetMapping("/users")
    ResponseEntity<List<User>> getAllUsers() {

        return new ResponseEntity<>(userService.allUsers(), HttpStatus.OK);
    }

    @PostMapping("/users")
    EntityModel<User> addNewUser(@RequestBody User user) {
        EntityModel<User> entityModel = assembler.toModel(userService.saveUser(user));
        return entityModel;
    }

    // Single item

    @GetMapping("/users/{id}")
    EntityModel<User> getSingleUser(@PathVariable Integer id) {

        return assembler.toModel(userService.findUserById(id));

    }

    @PutMapping("/users")
    ResponseEntity<User> updateExistingUser(@RequestBody User userUpdate) throws IllegalAccessException {
        HttpStatus status = userService.updateUser(userUpdate);
        return new ResponseEntity<>(userUpdate, status);
    }

    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
    }

    @GetMapping("/getRoles")
    ResponseEntity<List<Role>> getRoleSet() {
        return new ResponseEntity<>(userService.getRoleSet(), HttpStatus.OK);
    }



}
