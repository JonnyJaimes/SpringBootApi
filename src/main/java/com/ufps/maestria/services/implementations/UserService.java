package com.bezkoder.springjwt.services.implementations;
import com.bezkoder.springjwt.dto.UserDTO;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.security.services.UserDetailsImpl;
import com.bezkoder.springjwt.services.interfaces.UserServiceInterface;


import org.hibernate.Hibernate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import com.bezkoder.springjwt.models.Role;
import com.bezkoder.springjwt.models.ERole;

import com.bezkoder.springjwt.repository.RoleRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import java.util.Set;

@Service
public class UserService implements UserServiceInterface {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Edit User
    public User editUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        return userRepository.save(user);
    }

    // Delete User
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    // Get all users
    @Transactional
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        users.forEach(user -> Hibernate.initialize(user.getRoles())); // Initialize roles
        return users.stream()
                .map(user -> {
                    UserDTO userDTO = new UserDTO();
                    userDTO.setId(user.getId());
                    userDTO.setUsername(user.getUsername());
                    userDTO.setEmail(user.getEmail());
                    userDTO.setEnabled(user.isEnabled());

                    Set<String> roles = user.getRoles().stream()
                            .map(role -> role.getName().name())
                            .collect(Collectors.toSet());
                    userDTO.setRoles(roles);

                    return userDTO;
                })
                .collect(Collectors.toList());
    }




    // Create User
    @Override
    public void createUser(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername()) || userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Username or Email already exists.");
        }

        User user = new User(userDTO.getUsername(), userDTO.getEmail(), passwordEncoder.encode(userDTO.getPassword()));

        // Assign roles
        Set<Role> roles = new HashSet<>();
        if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
            userDTO.getRoles().forEach(roleName -> {
                Role role = roleRepository.findByName(ERole.valueOf(roleName))
                        .orElseThrow(() -> new RuntimeException("Error: Role " + roleName + " is not found."));
                roles.add(role);
            });
        } else {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role USER is not found."));
            roles.add(userRole);
        }

        user.setRoles(roles);
        userRepository.save(user);
    }

    // Get Users by Role
    @Override
    public List<UserDTO> getUserByRol(String rol) {
        List<User> users = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream().anyMatch(role -> role.getName().name().equalsIgnoreCase(rol)))
                .collect(Collectors.toList());

        return users.stream()
                .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.isEnabled(), user.getVerificationToken(), user.getTokenExpiryTime()))
                .collect(Collectors.toList());
    }

    // Password Reset
    @Override
    public void reestablecerContrasena(String email, String actualContraseña, String nuevaContraseña) {
        User user = userRepository.findByEmail(email);

        if (!passwordEncoder.matches(actualContraseña, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(nuevaContraseña));
        userRepository.save(user);
    }

    // Create Moderator
    @Override
    public void createEncargado(UserDTO userDTO) {
        User user = new User(userDTO.getUsername(), userDTO.getEmail(), passwordEncoder.encode(userDTO.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(ERole.ROLE_MODERATOR).orElseThrow(() -> new RuntimeException("Error: Role MODERATOR is not found.")));
        user.setRoles(roles);
        userRepository.save(user);
    }

    // Delete Moderator
    @Override
    public void deleteEncargado(String email) {
        User user = userRepository.findByEmail(email);
        userRepository.delete(user);
    }

    // Load User by Username for Authentication
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return UserDetailsImpl.build(user);
    }
}
