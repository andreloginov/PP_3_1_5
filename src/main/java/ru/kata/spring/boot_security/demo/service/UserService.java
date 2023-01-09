package ru.kata.spring.boot_security.demo.service;

import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import javax.persistence.EntityManager;
import java.util.*;

@Service
public class UserService implements UserDetailsService {

    /*private final EntityManager entityManager;*/
    private final UserRepository userRepository;
    private final RoleRepository repository;
    private final ApplicationContext applicationContext;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, ApplicationContext applicationContext) {
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


    public List<User> allUsers() {
        return userRepository.findAll();
    }


    public boolean saveUser(User user) {

        PasswordEncoder encoder = applicationContext.getBean("passwordEncoder", PasswordEncoder.class);
        User userById = user.getId() == null ? null : userRepository.findById(user.getId()).get();

            // если user's id null, то это новый user, соотв-но шифруем ноый пароль
        if (user.getId() == null) {
            user.setPassword(encoder.encode(user.getPassword()));
        } else {
            // update pass if its different
            if (!Objects.requireNonNull(userById).getPassword().equals(user.getPassword())) {
                user.setPassword(encoder.encode(user.getPassword()));
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


    public Optional<User> findByName(String name) {
        return Optional.ofNullable(userRepository.findByName(name));
    }

}
