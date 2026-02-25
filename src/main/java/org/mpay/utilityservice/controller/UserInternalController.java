package org.mpay.utilityservice.controller;

import lombok.RequiredArgsConstructor;
import org.mpay.utilityservice.entity.User;
import org.mpay.utilityservice.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/internal/users")
@RequiredArgsConstructor
public class UserInternalController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        // Automatically peppers and hashes via our PepperedPasswordEncoder
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null) {
            user.setRole("ADMIN");
        }

        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "status", "User created successfully",
                "username", savedUser.getUsername(),
                "role", savedUser.getRole()
        ));
    }

    @PutMapping("/update/{username}")
    public ResponseEntity<?> updateUser(@PathVariable String username, @RequestBody Map<String, String> updates) {
        return userRepository.findByUsername(username).map(user -> {

            if (updates.containsKey("password")) {
                // Automatically peppers and hashes the new password
                user.setPassword(passwordEncoder.encode(updates.get("password")));
            }

            if (updates.containsKey("role")) {
                user.setRole(updates.get("role"));
            }

            userRepository.save(user);
            return ResponseEntity.ok(Map.of(
                    "status", "User updated successfully",
                    "username", username
            ));

        }).orElse(ResponseEntity.notFound().build());
    }


}