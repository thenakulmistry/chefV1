package com.chef.V1.controller;

import com.chef.V1.entity.PasswordResetToken;
import com.chef.V1.entity.User;
import com.chef.V1.dto.UserDTO;
import com.chef.V1.repository.PasswordResetTokenRepository;
import com.chef.V1.repository.UserRepository;
import com.chef.V1.service.UserService;
import com.chef.V1.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
            String accessToken = jwtUtil.generateToken(userDTO.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(userDTO.getUsername());
            User user = userService.getByUsername(userDTO.getUsername());

            Map<String, Object> response = new HashMap<>();

            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
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

    @PostMapping("refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refresh_token");
        try{
            if(refreshToken != null && jwtUtil.validateToken(refreshToken)) {
                String username = jwtUtil.extractUsername(refreshToken);
                String newAccessToken = jwtUtil.generateToken(username);

                Map<String, String> response = new HashMap<>();
                response.put("accessToken", newAccessToken);
                return ResponseEntity.ok(response);
            }
            else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid refresh token"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        User user = userRepository.findByEmail(request.get("email")).orElse(null);
        if(user == null) { return ResponseEntity.ok(Map.of("message", "If an account with that email exists, a mail has been sent to reset the password")); }
        try{
            String tokenString = UUID.randomUUID().toString();
            PasswordResetToken passwordResetToken = new PasswordResetToken(tokenString, user);
            passwordResetTokenRepository.save(passwordResetToken);
            String resetLink = frontendUrl + "/reset-password?token=" + tokenString;
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(request.get("email"));
            message.setSubject("Reset Password");
            message.setText("To reset your password, click the link below:\n" +resetLink);
            mailSender.send(message);
            return ResponseEntity.ok(Map.of("message", "If an account with that email exists, a mail has been sent to reset the password"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error sending password reset link"));
        }
    }

    @PostMapping("reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String password = request.get("password");

        if (token == null || password == null || password.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token and new password are required."));
        }

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token).orElse(null);
        if(passwordResetToken == null || passwordResetToken.isExpired()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid or expired token"));
        }

        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        passwordResetTokenRepository.delete(passwordResetToken);
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }
}
