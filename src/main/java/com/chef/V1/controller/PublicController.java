package com.chef.V1.controller;

import com.chef.V1.entity.User;
import com.chef.V1.dto.UserDTO;
import com.chef.V1.service.UserService;
import com.chef.V1.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("health_check")
    public String healthCheck(){
        return "OK";
    }

    @PostMapping("register")
    public ResponseEntity<?> addUser(@RequestBody UserDTO userDTO) {
        try{
            userService.addNewUser(userDTO);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Registration failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword()));
            String jwt = jwtUtil.generateToken(userDTO.getUsername());
            User user = userService.getByUsername(userDTO.getUsername());
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("username", user.getUsername());
            userDetails.put("name", user.getName());
            userDetails.put("role", user.getRole());
            userDetails.put("email", user.getEmail());
            userDetails.put("number", user.getNumber());
            response.put("user", userDetails);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
         catch (Exception e) {
            return new ResponseEntity<>("Login failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
