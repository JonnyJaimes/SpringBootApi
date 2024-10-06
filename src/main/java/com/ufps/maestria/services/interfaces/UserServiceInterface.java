package com.ufps.maestria.services.interfaces;


import com.ufps.maestria.dto.UserDTO;
import com.ufps.maestria.dto.UserDTO2;
import com.ufps.maestria.models.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;


public interface UserServiceInterface extends UserDetailsService{
    
    public void createUser(UserDTO user);
    public void createUser2(UserDTO user);

    public List<UserDTO> getUserByRol(String rol);

    public void reestablecerContrasena(String email, String actualContraseña, String nuevaContraseña);

    public void createEncargado(UserDTO user);

    public void deleteEncargado(String email);

    User editUser(Long id, UserDTO userDTO);

     public void deleteUser(Long id);

    // In UserService.java (or UserServiceInterface)
    public List<UserDTO> getAllUsers();
}
