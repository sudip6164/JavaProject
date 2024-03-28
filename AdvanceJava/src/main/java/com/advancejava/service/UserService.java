package com.advancejava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.advancejava.model.Role;
import com.advancejava.model.User;
import com.advancejava.repository.RoleRepository;
import com.advancejava.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public void registerUser(User user) {
        // Perform user registration logic
        userRepository.save(user);
        
        // Assign CUSTOMER role to the user
        Role customerRole = roleRepository.findByName("CUSTOMER");
        user.getRoles().add(customerRole);
        userRepository.save(user);
    }
}

