package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.Set;

@Service
public class UserServiceImpl implements UserService{
    private UserDao userDao;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
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
            User admin = new User("admin","admin",44);
            User user = new User("user","user",32);

            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setLogin("admin");
            admin.setRoles(new Role("admin"));
            userDao.add(admin);

            user.setPassword(passwordEncoder.encode("user"));
            user.setLogin("user");
            user.setRoles(new Role("user"));
            userDao.add(user);
        }
    }
}
