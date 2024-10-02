package com.bezkoder.springjwt.controllers;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.bezkoder.springjwt.security.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.bezkoder.springjwt.models.ERole;
import com.bezkoder.springjwt.models.Role;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.request.LoginRequest;
import com.bezkoder.springjwt.payload.request.SignupRequest;
import com.bezkoder.springjwt.payload.response.JwtResponse;
import com.bezkoder.springjwt.payload.response.MessageResponse;
import com.bezkoder.springjwt.repository.RoleRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.security.jwt.JwtUtils;
import com.bezkoder.springjwt.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  EmailService   emailService;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    return ResponseEntity.ok(new JwtResponse(jwt,
                         userDetails.getId(),
                         userDetails.getUsername(),
                         userDetails.getEmail(),
                         roles));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest, HttpServletRequest request) throws MessagingException {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
    }

    // Create a new user's account with encoded password
    User user = new User(signUpRequest.getUsername(),
            signUpRequest.getEmail(),
            encoder.encode(signUpRequest.getPassword()));

    // Use the assignRoles method for role assignment
    Set<Role> roles = assignRoles(signUpRequest.getRole());
    user.setRoles(roles);

    // Save the user to the database
    userRepository.save(user);

    // Send verification email
    String siteURL = request.getRequestURL().toString().replace(request.getServletPath(), "");
    emailService.sendVerificationEmail(user, siteURL);

    return ResponseEntity.ok(new MessageResponse("User registered successfully! Please verify your email."));
  }





  @GetMapping("/verify")
  public ResponseEntity<?> verifyUser(@RequestParam("token") String token) {
    User user = userRepository.findByVerificationToken(token)
            .orElseThrow(() -> new IllegalStateException("Token not found"));

    if (user.isEnabled()) {
      return ResponseEntity.badRequest().body(new MessageResponse("Account is already verified."));
    }

    // Check if the token has expired
    if (user.getTokenExpiryTime().isBefore(LocalDateTime.now())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Token has expired. Please register again."));
    }

    user.setEnabled(true);
    user.setVerificationToken(null);
    user.setTokenExpiryTime(null);
    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("Account verified successfully!"));
  }



  private Set<Role> assignRoles(Set<String> strRoles) {
    Set<Role> roles = new HashSet<>();

    if (strRoles == null || strRoles.isEmpty()) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role.toLowerCase()) {
          case "admin":
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(adminRole);
            break;
          case "mod":
            Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(modRole);
            break;
          case "user":
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
            break;
          default:
            System.out.println("Invalid role: " + role);
            throw new RuntimeException("Error: Invalid role specified.");
        }
      });
    }

    // Log the roles that were assigned
    System.out.println("Assigned roles: " + roles);

    return roles;
  }




  @PostMapping("/logout")
  @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
  public ResponseEntity<?> logout(HttpServletRequest request) {
    // Get the token from the request header
    String token = request.getHeader("Authorization");

    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7); // Remove the "Bearer " prefix
      // Optionally: Invalidate the token by adding it to a blacklist (if implemented)
      // Example: tokenBlacklistService.addTokenToBlacklist(token);
    }

    // Invalidate token on the client side by notifying that the logout is successful
    return ResponseEntity.ok(new MessageResponse("User logged out successfully!"));
  }

}
