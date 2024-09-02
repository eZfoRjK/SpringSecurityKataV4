package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.Set;

@Service
public class UserServiceImpl implements UserService{
    private UserDao userDao;
    private final RoleDao roleDao;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserDao userDao, RoleDao roleDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.roleDao = roleDao;
    }

    @Transactional
    @Override
    public void add(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.add(user);
    }

    @Transactional
    @Override
    public Set<User> listUsers() {
        return userDao.listUsers();
    }

    @Transactional
    @Override
    public void removeUserById(Long id) {
        userDao.removeUserById(id);
    }

    @Override
    public User findUser(Long id) {
        return userDao.findUser(id);
    }

    @Transactional
    @Override
    public void update(User changedUser) {
        if (!(userDao.findUser(changedUser.getId())).getPassword().equals(changedUser.getPassword())) {
            changedUser.setPassword(passwordEncoder.encode(changedUser.getPassword()));
        }
        userDao.update(changedUser);
    }

    @Transactional
    @Override
    public void addAdmin() {
        if (userDao.ifDBEmpty()) {
            User admin = new User("admin", "admin", 44);
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setLogin("admin");;

            Role adminRole = roleDao.findByName("admin");
            if (adminRole == null) {
                adminRole = new Role("admin");
                roleDao.add(adminRole);
            }
            admin.getRoles().add(adminRole);
            userDao.add(admin);

            User user = new User("user", "user", 32);
            user.setPassword(passwordEncoder.encode("user"));
            user.setLogin("user");

            Role userRole = roleDao.findByName("user");
            if (userRole == null) {
                userRole = new Role("user");
                roleDao.add(userRole);
            }
            user.getRoles().add(userRole);
            userDao.add(user);
        }
    }
}
