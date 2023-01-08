package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import javax.management.InstanceAlreadyExistsException;
import javax.persistence.EntityManager;
import java.util.*;

@Service
public class UserService implements UserDetailsService {

    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final RoleRepository repository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.repository = roleRepository;
        this.entityManager = entityManager;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByName(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User '%s' not found", username));
        }

        return user;
        /*return new org.springframework.security.core.userdetails.User(
                user.getName(), user.getPassword(), user.getAuthorities()
        );*/
    }


    public User findUserById(Integer userId) {
        Optional<User> userFromDb = userRepository.findById(userId);
        return userFromDb.orElse(new User());
    }


    public List<User> allUsers() {
        return userRepository.findAll();
    }


    public boolean saveUser(User user) {

        BCryptPasswordEncoder passwordEncoder1 = new BCryptPasswordEncoder();

        User userFromDB = userRepository.findByName(user.getName());

        // if we create a user, we check the uniqueness of the name
        if (userFromDB != null && user.getId() == null) {
            //throw new InstanceAlreadyExistsException("The user already exists in the database.");
            return false;
        }

            // если user's id null, то это новый user, ставим ему пароль из поля password confirm
        if (user.getId() == null) {
            user.setPassword(passwordEncoder1.encode(user.getPassword()));
            //user.setRoles(Set.of(new Role(1, "ROLE_USER")));
        } else {
            //update
            User userById = userRepository.findById(user.getId()).get();
            if (!userById.getName().equals(user.getName()) && userFromDB != null) {
                // update user's name that is not unique
                return false;
            } else if (!userById.getPassword().equals(user.getPassword())) {
                user.setPassword(passwordEncoder1.encode(user.getPassword()));
            }
        }


        userRepository.save(user);

        return true;
    }

    public boolean deleteUser(Integer userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    public List<User> usergtList(Integer idMin) {
        return entityManager.createQuery("SELECT u FROM User u WHERE u.id > :paramId", User.class)
                .setParameter("paramId", idMin).getResultList();
    }

}
