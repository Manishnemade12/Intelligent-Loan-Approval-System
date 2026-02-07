package com.loanapproval.service;

import com.loanapproval.dto.AuthResponseDTO;
import com.loanapproval.dto.LoginRequestDTO;
import com.loanapproval.dto.UserDTO;
import com.loanapproval.entity.User;
import com.loanapproval.exception.UnauthorizedException;
import com.loanapproval.repository.UserRepository;
import com.loanapproval.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public AuthResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            String token = jwtTokenProvider.generateToken(authentication);
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new UnauthorizedException("User not found"));

            UserDTO userDTO = convertToDTO(user);

            return AuthResponseDTO.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .expiresIn(jwtTokenProvider.getJwtExpirationMs())
                    .user(userDTO)
                    .build();

        } catch (AuthenticationException ex) {
            log.error("Authentication failed for email: {}", loginRequest.getEmail());
            throw new UnauthorizedException("Invalid email or password");
        }
    }

    public UserDTO getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        return convertToDTO(user);
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .avatarUrl(user.getAvatarUrl())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
