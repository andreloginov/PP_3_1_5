package ru.kata.spring.boot_security.demo.service;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;

import java.util.*;

@Service
public class UserService implements UserDetailsService {

    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final RoleRepository repository;
    private final ApplicationContext applicationContext;

    public UserService(EntityManager entityManager, UserRepository userRepository, RoleRepository roleRepository, ApplicationContext applicationContext) {
        this.entityManager = entityManager;
        this.userRepository = userRepository;
        this.repository = roleRepository;
        this.applicationContext = applicationContext;
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

    public Optional<User> restFindUserById(Integer userId) {
        return userRepository.findById(userId);
    }


    public List<User> allUsers() {
        return userRepository.findAll();
    }


    public User saveUser(User user) {

        PasswordEncoder encoder = applicationContext.getBean("passwordEncoder", PasswordEncoder.class);
        User userById = user.getId() == null ? null : userRepository.findById(user.getId()).get();

        // если user's id null, то это новый user, соотв-но шифруем ноый пароль
        if (user.getId() == null) {
            user.setPassword(user.getPasswordConfirm());
            user.setPassword(encoder.encode(user.getPassword()));
        } else {
            // update pass if its different
            if (!Objects.requireNonNull(userById).getPassword().equals(user.getPassword())) {
                user.setPassword(encoder.encode(user.getPassword()));
            }

        }

        userRepository.save(user);

        return user;
    }

    public boolean deleteUser(Integer userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }


    public Optional<User> findByName(String name) {
        return Optional.ofNullable(userRepository.findByName(name));
    }

    public List<Role> getRoleSet() {
        return repository.findAll();
    }



    public HttpStatus updateUser(User userUpdate) throws IllegalAccessException {

        HttpStatus httpStatus = checkFields(userUpdate) ? HttpStatus.OK : HttpStatus.NOT_MODIFIED;
        if (httpStatus == HttpStatus.OK) {
            userRepository.save(userUpdate);
        }

        return httpStatus;

    }

    boolean checkFields(User user) throws IllegalAccessException {
        boolean status = true;
        Field[] fields = user.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);

            Object value = field.get(user);
            // this statement if is only for PUT (NOT POST)
            // that means if we have a put method just continue
            if (field.getName().equals("passwordConfirm")) {
                String passwordConfirm = (String) field.get(user);
                if (!passwordConfirm.isBlank()) {
                    PasswordEncoder encoder = applicationContext.getBean("passwordEncoder", PasswordEncoder.class);
                    user.setPassword(encoder.encode(passwordConfirm));
                }
                continue;
            }
            if (value == null || value.toString().isBlank() || user.getRoles().isEmpty()) {
                return false;
            }
        }
        return status;
    }
}
