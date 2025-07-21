package com.chef.V1.service;

import com.chef.V1.dto.UserDTO;
import com.chef.V1.entity.User;
import com.chef.V1.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> findAll() {return userRepository.findAll();}

    public void addNewUser(UserDTO userDTO){
        User user = new User();
        user.setName(userDTO.getName());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setNumber(userDTO.getNumber());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole("USER");
        user.setEnabled(false);
        userRepository.save(user);
    }

    public void addNewAdmin(UserDTO userDTO){
        User user = new User();
        user.setName(userDTO.getName());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setNumber(userDTO.getNumber());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole("ADMIN");
        user.setEnabled(true);
        userRepository.save(user);
    }

    public User findById(ObjectId id) { return userRepository.findById(id).orElse(null); }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void deleteByUsername(String username){ userRepository.deleteByUsername(username); }

    public void updateUser(UserDTO userDTO, String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setName(userDTO.getName());
            user.setEmail(userDTO.getEmail());
            user.setNumber(userDTO.getNumber());
            userRepository.save(user);
        } else throw new RuntimeException("User not found: " + username);
    }

    public void changePassword(String username, String currentPassword, String newPassword) {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new RuntimeException("User not found");

        // For users authenticated via Google, they won't have a password to match.
        // We can allow them to set a password for the first time without providing a current one.
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            if (!passwordEncoder.matches(currentPassword, user.getPassword()))
                throw new RuntimeException("Incorrect current password");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
