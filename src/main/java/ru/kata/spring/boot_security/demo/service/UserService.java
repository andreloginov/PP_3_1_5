package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import javax.persistence.EntityManager;
import java.util.*;

@Service
public class UserService implements UserDetailsService {

    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final RoleRepository repository;
    private ApplicationContext applicationContext;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.repository = roleRepository;
        this.entityManager = entityManager;
    }
    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
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

        //BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        User userFromDB = userRepository.findByName(user.getName());
        User userById = user.getId() == null ? null : userRepository.findById(user.getId()).get();

            // если user's id null, то это новый user, соотв-но шифруем ноый пароль
        if (user.getId() == null) {
            user.setPassword(encoder.encode(user.getPassword()));
            //user.setRoles(Set.of(new Role(1, "ROLE_USER")));
            // update pass if its different
        } else {
            if (!Objects.requireNonNull(userById).getPassword().equals(user.getPassword())) {
                user.setPassword(encoder.encode(user.getPassword()));
            } else {

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

    public Optional<User> findByName(String name) {
        return Optional.ofNullable(userRepository.findByName(name));
    }

}
