package com.ufps.maestria.services.implementations;

import com.ufps.maestria.models.ERole;
import com.ufps.maestria.models.Role;
import com.ufps.maestria.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    // This method checks and creates roles if they do not exist in the database
    @PostConstruct
    public void initRoles() {
        createRoleIfNotFound(ERole.ROLE_USER);
        createRoleIfNotFound(ERole.ROLE_MODERATOR);
        createRoleIfNotFound(ERole.ROLE_ADMIN);
    }

    private void createRoleIfNotFound(ERole roleName) {
        Optional<Role> role = roleRepository.findByName(roleName);
        if (role.isEmpty()) {
            Role newRole = new Role(roleName);
            roleRepository.save(newRole);
        }
    }
}