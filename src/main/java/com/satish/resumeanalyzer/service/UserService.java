package com.satish.resumeanalyzer.service;

import com.satish.resumeanalyzer.entity.User;
import com.satish.resumeanalyzer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Register User
    public User registerUser(User user) {

        // Check if email already exists
        User existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser != null) {
            return null;
        }

        return userRepository.save(user);
    }

    // Login User
    public User loginUser(String email, String password) {

        User user = userRepository.findByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            return user;
        }

        return null;
    }
}