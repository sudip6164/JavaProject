package com.advancejava.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.advancejava.model.User;
import com.advancejava.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {
	@Autowired
    private UserRepository userRepository;
	
    @Autowired
    private HttpSession session; 
    
	@GetMapping("/admin")
	public String firstPage(@ModelAttribute User user,Model model) {
		if (session.getAttribute("activeUser") == null) {
            return "redirect:/login"; // Redirect to login page if not logged in
        }
		List<User> allUsers = userRepository.findAll();
        
        // Filter users to include only those with role "CUSTOMER" (assuming role ID is 3)
        List<User> customers = allUsers.stream()
                                        .filter(u -> u.getRoles().stream().anyMatch(role -> role.getId() == 3))
                                        .collect(Collectors.toList());

        model.addAttribute("userList", customers);
		return "dashboardTemplates/dashboardIndex.html";
	}
	
	@GetMapping("/userTable")
	public String UserTable(@ModelAttribute User u, Model model) {
		if (session.getAttribute("activeUser") == null) {
            return "redirect:/login"; // Redirect to login page if not logged in
        }
		model.addAttribute("userList", userRepository.findAll());
		return "dashboardTemplates/userTable.html";
	}
	
	@GetMapping("/editUser/{id}")
	public String editUserData(@PathVariable int id, Model model) {
		if (session.getAttribute("activeUser") == null) {
            return "redirect:/login"; // Redirect to login page if not logged in
        }
	    User user = userRepository.getById(id);
	    model.addAttribute("userObject", user);
	    return "dashboardTemplates/editUser.html";
	}
	
	@PostMapping("/updateUser")
	public String updateUserData(@ModelAttribute User user,Model model) {
		if (session.getAttribute("activeUser") == null) {
            return "redirect:/login"; // Redirect to login page if not logged in
        }
	    userRepository.save(user);
	    return "redirect:/userTable";
	}

	
	@GetMapping("/deleteUser/{id}")
	public String deleteAData(@PathVariable int id,	Model model)
	{
		if (session.getAttribute("activeUser") == null) {
            return "redirect:/login"; // Redirect to login page if not logged in
        }
		userRepository.deleteById(id);
		model.addAttribute("userList",userRepository.findAll());
		return "redirect:/userTable";
	}
	
	@GetMapping("/dashboardlogout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // Invalidate the session to clear all session attributes
        }
        return "redirect:/"; // Redirect to the home page or any other appropriate page after logout
    }
	
}
