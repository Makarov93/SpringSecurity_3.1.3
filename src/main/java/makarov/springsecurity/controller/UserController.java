package makarov.springsecurity.controller;

import makarov.springsecurity.model.User;
import makarov.springsecurity.service.UserServiceImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class UserController {

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public String getUserProfile(Principal principal, Model model) {
        Long userId = userService.getUserByUsername(principal.getName()).getId();
        User currentUser = userService.getUserById(userId);

        if (currentUser != null) {
            model.addAttribute("user", currentUser);
            return "user";
        } else {
            model.addAttribute("errorMessage", "User not found");
            return "user";
        }
    }

    @GetMapping("/user/user_edit")
    public String editUserProfile(Principal principal, Model model) {
        if (principal != null) {
            Long userId = userService.getUserByUsername(principal.getName()).getId();
            User currentUser = userService.getUserById(userId);

            if (currentUser != null) {
                model.addAttribute("user", currentUser);
                return "/user_edit";
            } else {
                model.addAttribute("errorMessage", "User not found");
                return "/user_edit";
            }
        } else {
            model.addAttribute("errorMessage", "User session not found");
            return "/user_edit";
        }
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String updateUser(@ModelAttribute("user") User formUser, Principal principal, Model model) {
        String currentUsername = principal.getName();
        User existingUser = userService.getUserById(formUser.getId());

        if (existingUser != null && existingUser.getUsername().equals(currentUsername)) {
            existingUser.setUsername(formUser.getUsername());
            existingUser.setEmail(formUser.getEmail());
            existingUser.setPassword(formUser.getPassword());

            try {
                userService.saveUser(existingUser);
                model.addAttribute("successMessage", "Changes saved successfully!");
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(existingUser, existingUser.getPassword(),
                                SecurityContextHolder.getContext().getAuthentication().getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                return "redirect:/user";
            } catch (Exception e) {
                model.addAttribute("errorMessage", "Error saving user: " + e.getMessage());
                model.addAttribute("user", formUser);
                return "user";
            }
        } else {
            model.addAttribute("errorMessage", "User not found or not authorized to update");
            model.addAttribute("user", formUser);
            return "user";
        }
    }
}