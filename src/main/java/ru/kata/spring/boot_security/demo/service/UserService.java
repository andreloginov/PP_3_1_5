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

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
       /* if (userFromDB == null) {
            //user.setRoles(Collections.singleton(new Role(1, "ROLE_USER")));
            user.setRoles(Collections.singleton(new Role(1, "ROLE_USER")));
            user.setPassword(passwordEncoder1.encode(user.getPassword()));
        } else {
            //user.setPassword(userRepository.getById(user.getId()).getPassword());
        }*/

        // если user's id не равен null, то это обнолвение юзера
        // тогда user's password не трогаем, он наверно не должен меняться с поля, поле же закрыто по сути
        if (user.getId() != null) {
            System.out.println(".");
            System.out.println(".if user's not null.");
            System.out.println("users id = " + user.getId() + ", var password: " + user.getPassword() + ", var passwordConfirm: " + user.getPasswordConfirm());
            System.out.println("..");
            System.out.println(".");
        } else {
            // если user's id null, то это новый user, ставим ему пароль из поля password confirm
            user.setPassword(passwordEncoder1.encode(user.getPassword()));
            user.setRoles(Set.of(new Role(1, "ROLE_USER")));
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
