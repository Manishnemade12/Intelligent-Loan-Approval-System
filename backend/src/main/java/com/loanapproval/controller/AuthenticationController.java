package com.loanapproval.controller;

import com.loanapproval.dto.AuthResponseDTO;
import com.loanapproval.dto.LoginRequestDTO;
import com.loanapproval.dto.UserDTO;
import com.loanapproval.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and user management endpoints")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and receive JWT token")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        AuthResponseDTO response = authenticationService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Retrieve authenticated user information")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        UserDTO userDTO = authenticationService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user (token invalidation handled client-side)")
    public ResponseEntity<Void> logout() {
        // JWT is stateless, logout is handled by removing token on client side
        return ResponseEntity.ok().build();
    }
}
