package com.agrishop.controller;

import com.agrishop.model.User;
import com.agrishop.payload.response.MessageResponse;
import com.agrishop.payload.response.UserResponse;
import com.agrishop.repository.UserRepository;
import com.agrishop.security.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserProfile() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        return userRepository.findById(userDetails.getId())
                .map(this::mapUserToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateUserProfile(@RequestBody UserResponse userRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        return userRepository.findById(userDetails.getId())
                .map(user -> {
                    user.setName(userRequest.getName());
                    // Note: We don't update email or password here for security reasons
                    userRepository.save(user);
                    return ResponseEntity.ok(new MessageResponse("Profile updated successfully!"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private UserResponse mapUserToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}