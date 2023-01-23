package ru.kata.spring.boot_security.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;


    @Column(name = "surname")
    private String surName;

    @Column(name = "email")
    private String email;

    @Column(name = "age")
    private Integer age;

    @Column(name = "password")
    private String password;

    @Transient
    private String passwordConfirm;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_and_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Fetch(FetchMode.JOIN)
    private Set<Role> roles;

    public User() {
    }

    // *
    // *
    // *
    // * * * overridden methods of the UserDetails class * * *
    // *
    // *
    // *


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }




    // *
    // *
    // *
    // *** getters and setter ***
    // *
    // *
    // *


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    // using to display formatted roles (like 'USER, ADMIN') without a prefix ROLE_
    public String getPureRoles() {

        StringBuilder stringBuilder = new StringBuilder();
        String subStringToDelete = "ROLE_";
        roles.forEach(role -> stringBuilder.append(role.getName().substring(subStringToDelete.length())).append(", "));
        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length() - 1);

        return stringBuilder.toString();
    }

    public boolean deleteSingleRoleFromRoles(String role) {
        return roles.removeIf(currentRole -> currentRole.getName().contains(role));
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(name, user.name) && Objects.equals(surName, user.surName) && Objects.equals(email, user.email) && Objects.equals(age, user.age) && Objects.equals(password, user.password) && Objects.equals(passwordConfirm, user.passwordConfirm) && Objects.equals(roles, user.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surName, email, age, password, passwordConfirm, roles);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surName='" + surName + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", password='" + password + '\'' +
                ", passwordConfirm='" + passwordConfirm + '\'' +
                ", roles=" + roles +
                '}';
    }
}
