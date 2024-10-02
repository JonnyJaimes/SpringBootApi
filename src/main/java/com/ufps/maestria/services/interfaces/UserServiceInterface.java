package com.bezkoder.springjwt.services.interfaces;


import com.bezkoder.springjwt.dto.UserDTO;
import com.bezkoder.springjwt.models.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;


public interface UserServiceInterface extends UserDetailsService{
    
    public void createUser(UserDTO user);

    public List<UserDTO> getUserByRol(String rol);

    public void reestablecerContrasena(String email, String actualContraseña, String nuevaContraseña);

    public void createEncargado(UserDTO user);

    public void deleteEncargado(String email);

    User editUser(Long id, UserDTO userDTO);

     public void deleteUser(Long id);

    // In UserService.java (or UserServiceInterface)
    public List<UserDTO> getAllUsers();
}
