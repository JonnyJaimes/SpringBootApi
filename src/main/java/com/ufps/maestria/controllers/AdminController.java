package com.bezkoder.springjwt.controllers;


import com.bezkoder.springjwt.dto.AspiranteDTO;
import com.bezkoder.springjwt.dto.UserDTO;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.services.implementations.AspiranteService;
import com.bezkoder.springjwt.services.implementations.UserService;
import com.bezkoder.springjwt.services.interfaces.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
public class AdminController {


    private final UserServiceInterface userService;

    private final AspiranteService aspiranteService;


    // Constructor Injection

    public AdminController(UserServiceInterface userService, AspiranteService aspiranteService) {
        this.userService = userService;
        this.aspiranteService = aspiranteService;
    }

    // UserController.java
    @PutMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> editUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            User updatedUser = userService.editUser(id, userDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error editing user: " + e.getMessage());
        }
    }

    // UserController.java


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting user: " + e.getMessage());
        }
    }


    // Get all users (ADMIN and MODERATOR access)

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving users: " + e.getMessage());
        }
    }

    // Get all aspirantes (ADMIN and MODERATOR access)
    @GetMapping("/aspirantes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<List<AspiranteDTO>> getAllAspirantes() {
        List<AspiranteDTO> aspirantes = aspiranteService.getAllAspirantes();
        return ResponseEntity.ok(aspirantes);
    }


}
