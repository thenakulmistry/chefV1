package com.chef.V1.controller;

import com.chef.V1.entity.User;
import com.chef.V1.repository.UserRepository;
import com.chef.V1.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("auth/google")
public class GoogleAuthController {

    private static final Logger logger = LoggerFactory.getLogger(GoogleAuthController.class); // Added logger

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${app.frontend.google-redirect-uri}")
    private String frontendRedirectUri;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam String code) {
        try{
            String tokenEndpoint = "https://oauth2.googleapis.com/token";

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("redirect_uri", frontendRedirectUri);
            params.add("grant_type", "authorization_code");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

            logger.info("Exchanging code for token with Google. Redirect URI used: {}", frontendRedirectUri);
            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenEndpoint, requestEntity, Map.class);

            // Check if tokenResponse or its body is null, or if id_token is missing
            if (tokenResponse.getBody() == null || !tokenResponse.getBody().containsKey("id_token")) {
                logger.error("Failed to get id_token from Google. Response: {}", tokenResponse.getBody());
                Map<String, Object> errorBody = new HashMap<>();
                errorBody.put("message", "Failed to retrieve id_token from Google.");
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorBody);
            }
            String idToken = tokenResponse.getBody().get("id_token").toString();

            String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            logger.info("Fetching user info from Google.");
            ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);

            if(userInfoResponse.getStatusCode() == HttpStatus.OK && userInfoResponse.getBody() != null){
                Map<String, Object> userInfo = userInfoResponse.getBody();
                String email = (String) userInfo.get("email");
                String name = (String) userInfo.get("name");

                if (email == null) {
                    logger.error("Email not found in user info from Google. UserInfo: {}", userInfo);
                    Map<String, Object> errorBody = new HashMap<>();
                    errorBody.put("message", "Email not found in Google user profile.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
                }

                User user = userRepository.findByEmail(email).orElse(null);

                if (user == null) {
                    logger.info("User with email {} not found. Creating new user.", email);
                    user = new User();
                    user.setEmail(email);
                    user.setUsername(email);
                    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    user.setRole("USER");
                    user.setName(name);
                    userRepository.save(user); // Ensure 'user' is the saved entity
                } else {
                    logger.info("User with email {} found. ID: {}", email, user.getId());
                }

                String jwtToken = jwtUtil.generateToken(email);

                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("accessToken", jwtToken);

                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("username", user.getUsername());
                userMap.put("email", user.getEmail());
                userMap.put("role", user.getRole());
                userMap.put("name", user.getName());

                responseBody.put("user", userMap);

                return ResponseEntity.ok(responseBody);
            }
            logger.error("Failed to fetch user info from Google. Status: {}, Body: {}", userInfoResponse.getStatusCode(), userInfoResponse.getBody());
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("message", "Failed to fetch user info from Google.");
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorBody); // Return JSON error
        } catch (HttpStatusCodeException e) {
            // Catch exceptions from RestTemplate (e.g., 4xx or 5xx from Google)
            logger.error("HTTP error during Google OAuth flow: {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("message", "Error communicating with Google OAuth services.");
            errorBody.put("details", e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(errorBody);
        } catch (Exception e) {
            // Log the full stack trace for any other unexpected exceptions
            logger.error("Unexpected error during Google OAuth callback: ", e); // Log the full exception
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("message", "An internal server error occurred during Google authentication.");
            // Optionally, in dev, you might include e.getMessage(), but be cautious in prod.
            // errorBody.put("details", e.getMessage()); 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody); // Return JSON error
        }
    }
}
