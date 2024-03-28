package com.advancejava.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.advancejava.model.User;
import com.advancejava.repository.UserRepository;
import com.advancejava.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class SignupController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
   

//    @Autowired
//    private PasswordEncoder passwordEncoder; // Assuming you have a password encoder bean

    @GetMapping("/signup")
	public String signup(){
		return "login/signup.html";
	}
    
    @PostMapping("/register")
    public String signup(@ModelAttribute("user") User user, Model model) {
        // Check if the user already exists
        if (userRepository.findByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "User already exists!");
            return "login/signup.html";
        }

        // Encode the user's password before saving
//        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Register the user and assign CUSTOMER role
        userService.registerUser(user);

        // Redirect to login page after successful registration
        return "redirect:/login";
    }
    
	    @GetMapping("/login")
		public String login(){
			return "login/login.html";
		}
	    
	    @PostMapping("/postlogin")
		public String postLogin(@ModelAttribute User u,Model model, HttpSession session){
			if (userRepository.existsByEmailAndPassword(u.getEmail(), u.getPassword())) {
				session.setAttribute("activeUser", u.getEmail());
				List<User> userList=userRepository.findAll();
				model.addAttribute("userList",userList);
				return "home.html";
			}
			return "login/login.html";
			
		}
}
