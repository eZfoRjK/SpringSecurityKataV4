package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class UserController {

    private UserService userService;
    private RoleService roleService;

    @Autowired
    public UserController(UserService userService,RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/")
    public String printUsers() {
        return "redirect:/login";
    }

    @GetMapping("/admin")
    public String printUsers(ModelMap model) {
        model.addAttribute( "allUsers", userService.listUsers() );
        return "admin";
    }

    @GetMapping( "/user")
    public String printUser(Authentication authentication, ModelMap model) {
        model.addAttribute( "user", (User) authentication.getPrincipal());
        return "user";
    }

    @GetMapping("/admin/delete")
    public String deleteUser(@RequestParam(name = "id") Long id) {
        userService.removeUserById(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/new")
    public String newUser(@ModelAttribute("user") User user, ModelMap modelRole) {
        modelRole.addAttribute( "modelRole", roleService.rolesSet());
        return "/admin/addForm";
    }

    @PostMapping("/admin/new")
    public String addUser(@ModelAttribute("user") User user) {
        Set<Role> roles = new HashSet<>();
        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                try {
                    Long id = Long.parseLong(role.getRole());
                    Role roleFromDB = roleService.findRole(id);
                    if (roleFromDB != null) {
                        roles.add(roleFromDB);
                    } else {
                        System.out.println("Роль с id " + id + " не найдена");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка преобразования роли: " + role.getRole());
                }
            }
        }
        user.setRoles(roles);
        userService.add(user);
        return "redirect:/admin";
    }

    @GetMapping("/admin/change")
    public String changeUser(@RequestParam(name = "id") Long id, ModelMap model) {
        model.addAttribute("user", userService.findUser(id));
        model.addAttribute( "modelRole", roleService.rolesSet());
        model.addAttribute( "id", id);
        model.addAttribute( "roleSelect", new ArrayList<Long>());
        return "/admin/changeForm";
    }

    @PatchMapping("/admin/change")
    public String update(@RequestParam(required = false) List<Long> roleSelectedID,
                         @ModelAttribute("user") User changedUser) {
        Set<Role> roles = new HashSet<>();
        if (roleSelectedID != null) {
            for (Long roleIdFromFront : roleSelectedID) {
                Role role = roleService.findRole(roleIdFromFront);
                if (role != null) {
                    roles.add(role);
                } else {
                    System.out.println("Роль с id " + roleIdFromFront + " не найдена");
                }
            }
        }
        changedUser.setRoles(roles);
        userService.update(changedUser);
        return "redirect:/admin";
    }
}
