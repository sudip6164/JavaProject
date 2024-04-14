package com.advancejava.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.advancejava.model.User;
import com.advancejava.model.Role;
import com.advancejava.repository.UserRepository;
import com.advancejava.repository.RoleRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Collections;

import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.mindrot.jbcrypt.BCrypt;

@Controller
public class SignupController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private HttpSession session;
    
    @Autowired
    private JavaMailSender emailSender;

    private Map<String, String> otpMap = new HashMap<>();
    
    @GetMapping("/signup")
    public String signupPage() {
        return "signup.html";
    }
    
    @PostMapping("/register")
    public String signup(@ModelAttribute("user") User user, Model model) {
        // Check if the user already exists
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
            session.setAttribute("loggedIn", true);
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
        session.setAttribute("loggedIn", false);
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
   
    @GetMapping("/forgotPasswordPage")
    public String forgotPasswordPage() {
        return "forgotPassword.html";
    }

    @PostMapping("/forgotPassword")
    public String forgotPassword(@RequestParam("email") String email, Model model) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            model.addAttribute("invalidEmail", true);
            return "forgotPassword.html"; // Stay on the forgot password page
        }

        String otp = generateOTP();
        System.out.println("Generated OTP for email " + email + ": " + otp);
        otpMap.put(email, otp); // Store the OTP in the map with the email as the key
        sendOTPEmail(email, otp);
        
        model.addAttribute("email", email);
        // Redirect to the verifyOTP page with the email parameter
       return "verifyOTP.html";
    }


    private String generateOTP() {
        // Generate a 6-digit OTP
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private void sendOTPEmail(String email, String otp) {
        // Get current timestamp
        long timestamp = System.currentTimeMillis();
        // Combine OTP value and timestamp into a single string separated by ':'
        String otpWithTimestamp = otp + ":" + timestamp;
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setTo(email);
            helper.setSubject("Password Reset OTP");
            helper.setText("Your OTP for password reset is: " + otp+"\n\nYour OTP will expire in 5 minutes.");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        emailSender.send(message);
        // Store OTP with timestamp in the map
        otpMap.put(email, otpWithTimestamp);
    }


    @PostMapping("/verifyOTP")
    public String verifyOTP(@RequestParam("email") String email, @RequestParam("otp") String otp, Model model) {
        String storedOTP = otpMap.get(email); // Retrieve the OTP using the email as the key
        System.out.println("Email received in verifyOTP: " + email);
        System.out.println("OTP received in verifyOTP: " + otp);
        System.out.println("Retrieved OTP for email " + email + ": " + storedOTP);
        if (storedOTP != null) {
            // Split the storedOTP to extract OTP value and timestamp
            String[] storedOTPParts = storedOTP.split(":");
            String otpValue = storedOTPParts[0];
            long otpTimestamp = Long.parseLong(storedOTPParts[1]);
            long currentTimestamp = System.currentTimeMillis();

            // Check if OTP is expired (10 minutes)
            if (currentTimestamp - otpTimestamp > 300000) {
                // OTP is expired
                model.addAttribute("expiredOTP", true);
                return "forgotPassword.html";
            }

            if (otpValue.equals(otp)) {
            	model.addAttribute("email", email);
                // OTP is valid, allow the user to reset password
                return "resetPassword.html";
            } else {
                // OTP is invalid
                model.addAttribute("invalidOTP", true);
                return "forgotPassword.html";
            }
        } else {
            // No OTP found for the email
            model.addAttribute("invalidOTP", true);
            return "forgotPassword.html";
        }
    }

    
    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam("email") String email, @RequestParam("password") String password, Model model) {
       
        User user = userRepository.findByEmail(email);
        System.out.println(email);
        System.out.println(user);
        // Hash the new password
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        user.setPassword(hashedPassword);
        userRepository.save(user);

        // Clear OTP from map after password reset
        otpMap.remove(email);

        model.addAttribute("passwordResetSuccess", true);
        return "login.html"; // Redirect to login page after password reset
    }
    

}
