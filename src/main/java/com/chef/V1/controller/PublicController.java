package com.chef.V1.controller;

import com.chef.V1.entity.User;
import com.chef.V1.entity.UserDTO;
import com.chef.V1.service.UserDetailsServiceImpl;
import com.chef.V1.service.UserService;
import com.chef.V1.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
    private UserDetailsServiceImpl userDetailsService;
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
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword()));
            userDetailsService.loadUserByUsername(userDTO.getUsername());
            String jwt = jwtUtil.generateToken(userDTO.getUsername());
            User user = userService.getByUsername(userDTO.getUsername());
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("user", Map.of(
                    "username", user.getUsername(),
                    "name", user.getName(),
                    "role", user.getRole()
            ));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
