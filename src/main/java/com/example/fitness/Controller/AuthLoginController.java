package com.example.fitness.Controller;


import com.example.fitness.Entity.User;
import com.example.fitness.Service.Implementation.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthLoginController {
    private final UserServiceImpl userService;
    @Autowired
    public AuthLoginController(UserServiceImpl UserService) {
        this.userService = UserService;
    }

    @GetMapping("/register")
    public String registerForm(Model model, @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("user", new User());
        if (error != null) {
            model.addAttribute("error", "Email is already in use");
        }

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, @RequestParam("role") String role) {
        if (userService.findByEmail(user.getEmail()) != null) {
            return "redirect:/register?error";
        }
        user.setRole(role);
        userService.saveUser(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }
}