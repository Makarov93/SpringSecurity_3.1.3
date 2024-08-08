package makarov.springsecurity.controller;

import makarov.springsecurity.model.User;
import makarov.springsecurity.service.UserServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserServiceImpl userService;

    public AdminController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String adminPage(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "/admin";
    }

    @GetMapping("/admin_list")
    public String userList(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin_list";
    }

    @GetMapping("/admin_add")
    public String addUser(Model model) {
        model.addAttribute("user", new User());
        return "/admin_add";
    }

    @PostMapping("/added")
    public String addedUser(@ModelAttribute("user") User user) {
        userService.saveUser(user);
        return "redirect:/admin/admin_list";
    }

    @GetMapping("/admin_edit/{id}")
    public String editUser(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "admin_edit";
    }

    @PostMapping("/admin_edit")
    public String editUser(@ModelAttribute("user") User user) {
        userService.saveUser(user);
        return "redirect:/admin/admin_list";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/admin_list";
    }
}
