package com.ufps.maestria.controllers;


import com.ufps.maestria.dto.AspiranteDTO;
import com.ufps.maestria.dto.UserDTO;
import com.ufps.maestria.dto.UserDTO2;
import com.ufps.maestria.models.User;
import com.ufps.maestria.services.implementations.AspiranteService;
import com.ufps.maestria.services.interfaces.UserServiceInterface;
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


    public AdminController(UserServiceInterface userService, AspiranteService aspiranteService) {
        this.userService = userService;
        this.aspiranteService = aspiranteService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-user")
    public ResponseEntity<?> createUser2(@RequestBody UserDTO userDTO) {
        try {
            userService.createUser2(userDTO);  // Use the service method to create the user
            return new ResponseEntity<>("User created successfully.", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
