package com.risktracker.service;

import com.risktracker.dto.LoginRequest;
import com.risktracker.dto.LoginResponse;
import com.risktracker.dto.UserDTO;
import com.risktracker.exception.ResourceNotFoundException;
import com.risktracker.model.User;
import com.risktracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Authenticate user login
     */
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid username or password"));
        
        if (!user.getActive()) {
            throw new RuntimeException("User account is disabled");
        }
        
        // In production, use proper password hashing (BCrypt)
        if (!user.getPassword().equals(request.getPassword())) {
            throw new ResourceNotFoundException("Invalid username or password");
        }
        
        // Generate simple token (in production, use JWT)
        String token = UUID.randomUUID().toString();
        
        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole(),
                token
        );
    }
    
    /**
     * Get all users
     */
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get user by ID
     */
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToDTO(user);
    }
    
    /**
     * Create new user
     */
    public UserDTO createUser(UserDTO userDTO) {
        // Check if username already exists
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setFullName(userDTO.getFullName());
        user.setPassword(userDTO.getPassword()); // In production, hash the password
        user.setRole(userDTO.getRole() != null ? userDTO.getRole() : "USER");
        user.setActive(userDTO.getActive() != null ? userDTO.getActive() : true);
        
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }
    
    /**
     * Update user
     */
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        // Check if new username conflicts with existing user
        if (!user.getUsername().equals(userDTO.getUsername())) {
            if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
                throw new RuntimeException("Username already exists");
            }
            user.setUsername(userDTO.getUsername());
        }
        
        user.setFullName(userDTO.getFullName());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(userDTO.getPassword()); // In production, hash the password
        }
        user.setRole(userDTO.getRole());
        user.setActive(userDTO.getActive());
        
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }
    
    /**
     * Delete user
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
    
    /**
     * Get users by role
     */
    public List<UserDTO> getUsersByRole(String role) {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole().equals(role))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert User entity to UserDTO
     */
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole(),
                user.getActive()
        );
    }
}
