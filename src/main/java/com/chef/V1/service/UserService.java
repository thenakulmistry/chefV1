package com.chef.V1.service;

import com.chef.V1.entity.User;
import com.chef.V1.entity.UserDTO;
import com.chef.V1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void addNewUser(UserDTO userDTO){
        User user = new User();
        user.setName(userDTO.getName());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setNumber(userDTO.getNumber());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole("USER");
        userRepo.save(user);
    }

    public void addNewAdmin(UserDTO userDTO){
        User user = new User();
        user.setName(userDTO.getName());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setNumber(userDTO.getNumber());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole("ADMIN");
        userRepo.save(user);
    }

    public User getByUsername(String username){return userRepo.findByUsername(username);}

    public void deleteByUsername(String username){userRepo.deleteByUsername(username);}

    public void updateUser(UserDTO userDTO, String username){
        User user = userRepo.findByUsername(username);
        if(userDTO.getName() != null) user.setName(userDTO.getName());
        if(userDTO.getNumber() != null) user.setNumber(userDTO.getNumber());
        if(userDTO.getPassword() != null) user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        if(userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());
        if(userDTO.getUsername() != null) user.setUsername(userDTO.getUsername());
        userRepo.save(user);
    }
}
