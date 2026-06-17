package com.satish.resumeanalyzer.controller;

import com.satish.resumeanalyzer.entity.User;
import com.satish.resumeanalyzer.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // Register Page
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // Register User
    @PostMapping("/register")
    public String registerUser(User user, Model model) {

        User savedUser = userService.registerUser(user);

        if (savedUser == null) {
            model.addAttribute("error", "Email already registered!");
            return "register";
        }

        model.addAttribute("success", "Registration Successful! Please Login.");
        return "login";
    }

    // Login Page
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Login User
    @PostMapping("/login")
    public String loginUser(
            @RequestParam String email,
            @RequestParam String password,
            Model model,
            HttpSession session) {

        User user = userService.loginUser(email, password);

        if (user != null) {

            session.setAttribute("loggedUser", user);
            session.setAttribute("username", user.getName());

            return "redirect:/upload";
        }

        model.addAttribute("error", "Invalid Email or Password");
        return "login";
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/home";
    }
}