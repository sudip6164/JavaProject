package com.advancejava.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.advancejava.model.User;
import com.advancejava.model.Role;
import com.advancejava.repository.UserRepository;
import com.advancejava.repository.RoleRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;

@Controller
public class SignupController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private HttpSession session;
    
    @GetMapping("/signup")
   	public String signupPage() {
   		return "signup.html";
   	}
    
    @PostMapping("/register")
    public String signup(@ModelAttribute("user") User user, Model model) {
         //Check if the user already exists
        if (userRepository.findByEmail(user.getEmail()) != null) {
            model.addAttribute("errorEmailExists", "User with email already exists!");
            return "signup.html"; // Stay as the signup modal
        }

        // Hash the password
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        
        // Perform user registration logic
        userRepository.save(user);
        
        // Assign CUSTOMER role to the user
        Role customerRole = roleRepository.findByName("CUSTOMER");
        user.getRoles().add(customerRole);
        userRepository.save(user);

        return "index.html";
    }

    @GetMapping("/login")
	public String loginPage() {
		return "login.html";
	}
    
    @PostMapping("/postlogin")
    public String postLogin(@ModelAttribute User u, Model model) {
        User user = userRepository.findByEmail(u.getEmail());
        //if user exists
        if (user != null && BCrypt.checkpw(u.getPassword(), user.getPassword())) {
            session.setAttribute("activeUser", user.getEmail());
            session.setAttribute("fullname", user.getFullname());
            model.addAttribute("loggedIn", true);
            model.addAttribute("userList", userRepository.findAll());
            if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"))) {
                // If the user has the "ADMIN" role, redirect to the admin dashboard
                return "redirect:/admin";
            } else {
                // For other users, redirect to the home page or any other appropriate page
                model.addAttribute("userList", userRepository.findAll());
                return "index.html";
            }
        }
        //else
        model.addAttribute("loggedIn", false);
        model.addAttribute("loginError", true);
		return "login.html";
    }
    
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // Invalidate the session to clear all session attributes
        }
        return "redirect:/"; // Redirect to the home page or any other appropriate page after logout
    }
   
}
