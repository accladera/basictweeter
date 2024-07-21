package com.accladera.springsecurity.controller;

import com.accladera.springsecurity.controller.dto.CreateUserDto;
import com.accladera.springsecurity.entities.Role;
import com.accladera.springsecurity.entities.UserP;
import com.accladera.springsecurity.repository.RoleRepository;
import com.accladera.springsecurity.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
public class UserController {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository,
                          RoleRepository roleRepository,
                          BCryptPasswordEncoder passwordEncoder
                          ){
        this.userRepository=  userRepository;
        this.roleRepository=roleRepository;
        this.passwordEncoder= passwordEncoder;
    }

    @Transactional
    @PostMapping("/users")
    public ResponseEntity<Void> newUser(@RequestBody CreateUserDto dto){

        var basicRole= roleRepository.findByName(Role.Values.BASIC.name());
        var userFromDb =userRepository.findByUsername(dto.username());
        if (userFromDb.isPresent()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        var user = new UserP();
        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRoles(Set.of(basicRole));
        userRepository.save(user);
        return ResponseEntity.ok().build();

    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public ResponseEntity<List<UserP>> listUsers(){
        var users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
}
